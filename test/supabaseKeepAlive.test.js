/*
 * Copyright (C) 2026 AKASHI DEV
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

import assert from 'node:assert/strict';
import test from 'node:test';
import { supabaseClient, keepAliveSupabase } from '../src/services/supabaseClient.js';

// Setup mock localStorage for Node test runner
const memoryStore = new Map();
const mockLocalStorage = {
  getItem: (key) => memoryStore.get(key) ?? null,
  setItem: (key, val) => memoryStore.set(key, String(val)),
  removeItem: (key) => memoryStore.delete(key),
  clear: () => memoryStore.clear()
};

globalThis.localStorage = mockLocalStorage;

test('supabase client configuration and fallback values', () => {
  assert.equal(supabaseClient.isConfigured(), false);
  supabaseClient.setConfig({
    url: 'https://furkfhzvvgxlyxukqomm.supabase.co',
    anonKey: 'sb_publishable_EhOKOAExG8w5rcZIYhaCdw_JcI3Y00f'
  });
  assert.equal(supabaseClient.isConfigured(), true);
  assert.ok(supabaseClient.getUrl().includes('furkfhzvvgxlyxukqomm'));
  assert.equal(supabaseClient.getAnonKey(), 'sb_publishable_EhOKOAExG8w5rcZIYhaCdw_JcI3Y00f');
  supabaseClient.setConfig({ url: '', anonKey: '' });
  assert.equal(supabaseClient.isConfigured(), false);
});

test('supabase keepAlive skips when last ping is within 4 days', async (t) => {
  const originalFetch = globalThis.fetch;
  let fetchCalled = false;
  globalThis.fetch = async () => {
    fetchCalled = true;
    return { ok: true, json: async () => [] };
  };
  t.after(() => {
    globalThis.fetch = originalFetch;
    mockLocalStorage.clear();
  });

  // Set last ping to 1 day ago
  const oneDayAgo = Date.now() - 24 * 60 * 60 * 1000;
  mockLocalStorage.setItem('veyra_supabase_last_ping', String(oneDayAgo));

  const result = await supabaseClient.keepAlive(false);
  assert.equal(result, false);
  assert.equal(fetchCalled, false);
});

test('supabase keepAlive sends request when last ping is older than 4 days', async (t) => {
  const originalFetch = globalThis.fetch;
  let requestedUrl = null;
  globalThis.fetch = async (url) => {
    requestedUrl = String(url);
    return { ok: true, json: async () => [] };
  };
  t.after(() => {
    globalThis.fetch = originalFetch;
    mockLocalStorage.clear();
  });

  // Set last ping to 5 days ago
  const fiveDaysAgo = Date.now() - 5 * 24 * 60 * 60 * 1000;
  mockLocalStorage.setItem('veyra_supabase_last_ping', String(fiveDaysAgo));

  const result = await supabaseClient.keepAlive(false);
  assert.equal(result, true);
  assert.ok(requestedUrl.includes('/rest/v1/track_analysis?select=video_id&limit=1'));

  const updatedPing = Number(mockLocalStorage.getItem('veyra_supabase_last_ping'));
  assert.ok(updatedPing > fiveDaysAgo);
});

test('supabase keepAlive force sends ping immediately', async (t) => {
  const originalFetch = globalThis.fetch;
  let fetchCount = 0;
  globalThis.fetch = async () => {
    fetchCount++;
    return { ok: true, json: async () => [] };
  };
  t.after(() => {
    globalThis.fetch = originalFetch;
    mockLocalStorage.clear();
  });

  mockLocalStorage.setItem('veyra_supabase_last_ping', String(Date.now()));

  const result = await keepAliveSupabase(true);
  assert.equal(result, true);
  assert.equal(fetchCount, 1);
});

test('supabase keepAlive handles network errors gracefully', async (t) => {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async () => {
    throw new Error('Network unreachable');
  };
  t.after(() => {
    globalThis.fetch = originalFetch;
    mockLocalStorage.clear();
  });

  const result = await supabaseClient.keepAlive(true);
  assert.equal(result, false);
});
