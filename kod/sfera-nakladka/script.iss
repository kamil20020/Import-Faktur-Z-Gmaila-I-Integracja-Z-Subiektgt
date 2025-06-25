[Setup]
AppName=Integracja Gmail i Subiekt GT ze Sferą
AppVersion=1.0
DefaultDirName={pf}\Integracja-Gmail-SubiektGt-Sfera
DefaultGroupName=Integracja-Gmail-SubiektGt-Sfera
OutputBaseFilename=Integracja-Gmail-SubiektGt-Sfera

[Files]
Source: "aplikacja-gmail_subiekt-integracja.jar"; DestDir: "{app}"
Source: "auth-data.json"; DestDir: "{app}"
Source: "schemas\*"; DestDir: "{app}\schemas"; Flags: recursesubdirs

Source: "php\*"; DestDir: "{app}\php"; Flags: recursesubdirs
Source: "htdocs\*"; DestDir: "{app}\htdocs"; Flags: recursesubdirs

Source: "start.bat"; DestDir: "{app}"
Source: "script.vbs"; DestDir: "{app}"

[Icons]
Name: "{commondesktop}\Uruchom Integracja Gmail i Subiekt GT ze Sferą"; Filename: "{app}\script.vbs"
