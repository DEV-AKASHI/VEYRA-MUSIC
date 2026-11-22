/*
 * Copyright (C) 2026 AKASHI DEV
 * Veyra Music - Modern Industrial & Neumorphic Audio Player
 * AGPL-3.0-or-later
 */

Unicode True
ManifestSupportedOS win10
RequestExecutionLevel user
SetCompressor /SOLID lzma
BrandingText "Veyra Music • AKASHI DEV"
Icon "${ICON}"
UninstallIcon "${ICON}"

!include "MUI2.nsh"

Name "Veyra Music"
OutFile "${OUTPUT}"
InstallDir "$LOCALAPPDATA\Programs\Veyra Music"
InstallDirRegKey HKCU "Software\Veyra Music" "InstallLocation"

VIProductVersion "${VERSION}.0"
VIFileVersion "${VERSION}.0"
VIAddVersionKey "ProductName" "Veyra Music"
VIAddVersionKey "FileVersion" "${VERSION}"
VIAddVersionKey "ProductVersion" "${VERSION}"
VIAddVersionKey "FileDescription" "Veyra Music Desktop Installer"
VIAddVersionKey "LegalCopyright" "Copyright (C) 2026 AKASHI DEV"
VIAddVersionKey "CompanyName" "AKASHI DEV"

!define MUI_ABORTWARNING
!define MUI_ICON "${ICON}"
!define MUI_UNICON "${ICON}"
!define MUI_FINISHPAGE_RUN "$INSTDIR\veyra.exe"
!define MUI_FINISHPAGE_RUN_TEXT "Iniciar Veyra Music"

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "${LICENSE}"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "Spanish"
!insertmacro MUI_LANGUAGE "English"

Section "Veyra Music" VeyraMusic
  SetShellVarContext current
  RMDir /r "$INSTDIR"
  SetOutPath "$INSTDIR"
  File /r "${BUNDLE}\*"
  WriteUninstaller "$INSTDIR\Uninstall Veyra Music.exe"

  ; Limpiar rastros y accesos directos anteriores
  Delete "$SMPROGRAMS\Orchard.lnk"
  Delete "$SMPROGRAMS\Orchard\Orchard.lnk"
  Delete "$DESKTOP\Orchard.lnk"
  RMDir "$SMPROGRAMS\Orchard"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\dev.sfg.orchard"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Orchard"
  DeleteRegKey HKCU "Software\Orchard Packages"

  Delete "$SMPROGRAMS\Veyra Music.lnk"
  CreateDirectory "$SMPROGRAMS\Veyra Music"
  CreateShortCut "$SMPROGRAMS\Veyra Music\Veyra Music.lnk" "$INSTDIR\veyra.exe" "" "$INSTDIR\icon.ico" 0 SW_SHOWNORMAL "" "Veyra Music - AKASHI DEV"
  CreateShortCut "$DESKTOP\Veyra Music.lnk" "$INSTDIR\veyra.exe" "" "$INSTDIR\icon.ico" 0 SW_SHOWNORMAL "" "Veyra Music - AKASHI DEV"

  WriteRegStr HKCU "Software\Veyra Music" "InstallLocation" "$INSTDIR"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "DisplayName" "Veyra Music"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "DisplayVersion" "${VERSION}"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "Publisher" "AKASHI DEV"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "InstallLocation" "$INSTDIR"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "DisplayIcon" "$INSTDIR\icon.ico"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "UninstallString" "$\"$INSTDIR\Uninstall Veyra Music.exe$\""
  WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "NoModify" 1
  WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "NoRepair" 1
SectionEnd

Section "Uninstall"
  SetShellVarContext current
  Delete "$SMPROGRAMS\Veyra Music\Veyra Music.lnk"
  RMDir "$SMPROGRAMS\Veyra Music"
  Delete "$DESKTOP\Veyra Music.lnk"
  RMDir /r "$INSTDIR"
  DeleteRegKey HKCU "Software\Veyra Music"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music"
SectionEnd
