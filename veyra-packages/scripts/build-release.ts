/*
 * Copyright (C) 2026 AKASHI DEV
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
 * mantenido únicamente para consumo personal y sin afiliación con el
 * proyecto original.
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

import { cp, mkdir, readFile, rm, chmod } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { detectTarget, isTarget, TARGETS, type Target } from "../src/backend/target.ts";

const projectRoot = path.resolve(import.meta.dir, "..");
const supportedShellTargets: Target[] = [...TARGETS];
const shellSuffixes: Record<Target, string> = {
  "linux-x64": "linux_x64",
  "linux-arm64": "linux_arm64",
  "win32-x64": "win_x64.exe",
  // Neutralino no tiene shell para Windows ARM64. Windows ejecuta su shell x64 bajo
  // emulación mientras el backend nativo Go de ARM64 selecciona los assets de Veyra Music para ARM64.
  "win32-arm64": "win_x64.exe",
  "darwin-x64": "mac_x64",
  "darwin-arm64": "mac_arm64"
};

function run(command: string, args: string[]): Promise<void> {
  return new Promise((resolve, reject) => {
    const child = spawn(command, args, { cwd: projectRoot, stdio: "inherit", windowsHide: true });
    child.on("error", reject);
    child.on("close", (code) => code === 0 ? resolve() : reject(new Error(`${command} exited with ${code}.`)));
  });
}

const requested = process.argv[2] ?? "host";
let targets: Target[];
if (requested === "all") targets = supportedShellTargets;
else {
  const target = requested === "host" ? detectTarget() : requested;
  if (!isTarget(target)) throw new Error(`Unknown release target: ${target}`);
  targets = [target];
}

await run("bun", ["run", "build:frontend"]);
await run("bun", ["run", "scripts/build-backend.ts", "host", "--extension"]);
await run("bunx", ["neu", "update"]);
await run("bunx", ["neu", "build"]);

const packageJson = JSON.parse(await readFile(path.join(projectRoot, "package.json"), "utf8")) as { version: string };
const releasesRoot = path.join(projectRoot, "dist", "releases");

async function resolveNeutralinoOutput(): Promise<string> {
  const primary = path.join(projectRoot, "dist", "neutralino", "veyra-packages");
  try {
    await readFile(path.join(primary, "resources.neu"));
    return primary;
  } catch {
    return path.join(projectRoot, "dist", "neutralino", "orchard-packages");
  }
}

async function resolveShellBinary(neutralinoDir: string, target: Target): Promise<string> {
  const suffix = shellSuffixes[target];
  const candidates = [
    path.join(neutralinoDir, `veyra-packages-${suffix}`),
    path.join(neutralinoDir, `orchard-packages-${suffix}`)
  ];
  for (const candidate of candidates) {
    try {
      await readFile(candidate);
      return candidate;
    } catch {}
  }
  return candidates[0];
}

const neutralinoOutput = await resolveNeutralinoOutput();

for (const target of targets) {
  await run("bun", ["run", "scripts/build-backend.ts", target]);
  const windows = target.startsWith("win32-");
  const shellBinary = await resolveShellBinary(neutralinoOutput, target);

  const bundleRoot = path.join(releasesRoot, `veyra-packages-${packageJson.version}-${target}`);
  const veyraAppName = windows ? "veyra-packages.exe" : "veyra-packages";
  const veyraBackendName = windows ? "veyra-packages-backend.exe" : "veyra-packages-backend";

  const extensionRoot = path.join(bundleRoot, "extensions");
  await rm(bundleRoot, { recursive: true, force: true });
  await mkdir(extensionRoot, { recursive: true });

  await Promise.all([
    cp(shellBinary, path.join(bundleRoot, veyraAppName)),
    cp(path.join(neutralinoOutput, "resources.neu"), path.join(bundleRoot, "resources.neu")),
    cp(
      path.join(projectRoot, "dist", "backend", target, veyraBackendName),
      path.join(extensionRoot, veyraBackendName)
    )
  ]);

  if (!windows) {
    await chmod(path.join(bundleRoot, veyraAppName), 0o755);
    await chmod(path.join(extensionRoot, veyraBackendName), 0o755);
  }
  console.log(`${target}: ${bundleRoot}`);
}

