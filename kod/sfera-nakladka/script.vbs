Set WshShell = CreateObject("WScript.Shell")
Set Wmi = GetObject("winmgmts:\\.\root\CIMV2")

' Uruchom PHP bez konsoli
WshShell.Run "php\php.exe -c php\php.ini-production -S 127.0.0.1:80 -t htdocs", 0, False

WScript.Sleep 1000

' Uruchom Javę bez konsoli
WshShell.Run "javaw -jar aplikacja-allegro_subiekt-integracja.jar", 0, False

' Funkcja sprawdzająca, czy javaw działa
Function IsJavaRunning()
    Dim processes, proc
    IsJavaRunning = False
    Set processes = Wmi.ExecQuery("Select * from Win32_Process Where Name = 'javaw.exe'")
    For Each proc In processes
        IsJavaRunning = True
        Exit For
    Next
End Function

' Czekaj aż javaw się zakończy
Do While IsJavaRunning()
    WScript.Sleep 1000 ' czekaj sekundę
Loop

' Po zamknięciu Javy zakończ php
WshShell.Run "taskkill /IM php.exe /F", 0, True