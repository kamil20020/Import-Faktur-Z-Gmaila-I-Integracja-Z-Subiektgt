[Setup]
AppName=Integracja Allegro i SubiektGT ze Sferą
AppVersion=1.0
DefaultDirName={pf}\Integracja-Allegro-SubiektGt-Sfera
DefaultGroupName=Integracja-Allegro-SubiektGt-Sfera
OutputBaseFilename=Integracja-Allegro-SubiektGt-Sfera

[Files]
Source: "aplikacja-allegro_subiekt-integracja.jar"; DestDir: "{app}"
Source: "auth-data.json"; DestDir: "{app}"

Source: "php\*"; DestDir: "{app}\php"; Flags: recursesubdirs
Source: "htdocs\*"; DestDir: "{app}\htdocs"; Flags: recursesubdirs

Source: "start.bat"; DestDir: "{app}"

[Icons]
Name: "{commondesktop}\Uruchom Integracja Allegro i SubiektGT ze Sferą"; Filename: "{app}\start.bat"
