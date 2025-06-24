@echo off

cd /d "%~dp0"

start "" /b php\php.exe -c php\php.ini-production -S 127.0.0.1:80 -t htdocs
set PHP_PID=%!

start "" /b javaw -jar aplikacja-allegro_subiekt-integracja.jar
set JAVA_PID=%!

pause