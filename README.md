# Integracja Gmail i SubiektGT - faktury zakupu

## Wstęp

Repozytorium dotyczy aplikacji umożliwiającej pobranie z Gmail faktur zakupu w formacie pdf, odczytanie danych z tych faktur i następnie utworzenie na ich pdostawie faktur zakupu w SubiektGT.

W aplikacji pobierane są na początku wiadomości zawierające załączniki pdf dostępne w danej skrzynce Gmail. Następnie użytkownik może wybrać, które pdfy powinny być zapisane jako faktury zakupu w SubiektGT. Aplikacja wykorzystuje do komunikacji z Subiektem dodatek Sfera, dzięki czemu możliwe jest całkiem sprawne tworzenie faktur zakupu w SubiektGT. 

Do wydobycia danych o fakturach z plików pdf wykorzystywane są szablony faktur zakupu. Istotne jest, że aby dany pdf mógł być odczytany jako faktura zakupu, konieczne jest wcześniejsze zdefiniowanie szablonu dla tej faktury np. wskazanie w aplikacji w którym miejscu znajduje się na pliku pdf miejsce wystawienia faktury, czy data wystawienia faktury. Dla każdej firmy wymagane jest utworzenie nowego szablonu faktury zakupu.

Aplikacja obsługuje sytuacje, w których wiadomości e-mail zawierają wiele załączników o formacie pdf. W tym przypadku w aplikacji będą wyświetlane załączniki w osobnych wierszach w tabeli.

### Sfera

Jak już wspomniano, wykorzystanie Sfery umożliwia usprawnienie tworzenia faktur zakupu. Bez tej wtyczki, konieczne byłoby bazowanie na plikach, co byłoby dosyć kłopotliwe. Wtedy trzeba by było dla każdej faktury w pdf tworzyć plik z fakturą zakupu w formacie obsługiwanym przez SubiektGT. Następnie każdy plik należałoby zaimportować do Subiekta poprzez opcję Dodaj na podstawie. Jest to więcej roboty. Dzięki SubiektGT w aplikacji integracja wystarczy jedno kliknięcie typu Zapisz faktury zakupu w SubiektGT. Działa to dlatego, że Sfera umożliwia zestawienie API typu COM w SubiektGT i następnie poprzez to API można wysyłać żądania. Technologia COM jest często związana z językiem C#. Dodatek Sfera jest płatny, lecz na szczęście jednorazowo.

### Nakładka na Sferę

Jak już wspomniałem Sfera jest raczej związana z językiem C#. Ja niestety nie znam za bardzo tego języka i ograniczyłem się do Javy. Dlatego wykorzystałem nakładkę na Sfere napisaną przez kogoś innego w php. Wtyczka jest zamieszczona na 
[repozytorium GitHub Lukegpl](https://github.com/Lukegpl/api-subiekt-gt?tab=readme-ov-file).

Nakładka ta okazała się całkiem dobra. Przede wszystkim używa REST i JSON oraz jest dosyć dobrze 
[udokomentowana](https://github.com/Lukegpl/api-subiekt-gt/wiki/API-Dokumentacja-v.-1.0). Są szczegółowe informacje o realizowanych funkcjach np. jakie dane są wymagane dla poszczególnych endpointów oraz co prawdopodobnie będzie zwrócone. 

Niestety musiałem też trochę przerobić kod tej nakładki, aby spełniała moje potrzeby np. wprowadziłem możliwość tworzenia faktur zakupu, gdzie wcześniej było możliwe tworzenie bodajże jedynie faktur sprzedaży i paragonów zwykłych. Dodatkowo dałem tworzenie klienta jednorazowego przy zapisywaniu faktur, a we wcześniejszej wersji nakładki, klienci musieli już istnieć w Subiekcie przed dodaniem nowych obiektów. Dodatkowo w nakładce dodanie faktury musiało być poprzedzone dodaniem zamówienia od klienta. Również to zmieniłem i u mnie mogą być odrazu dodane dokumenty do Subiekta. Ostatnią rzecz jaką zmieniłem, którą pamietam, było dodanie możliwości tworzenia produktów, gdy nie istnieją one w SubiektGT.

### Wykorzystanie aplikacji

Aplikacja jest wykorzystywana produkcyjnie przez mojego znajomego zajmującego się sprzedażą w Allegro i zarządzaniem finansami i magazynem w Subiekt GT. Na chwile obecną jest zadowolony i w razie czego wprowadzam drobne poprawki. Najmniej przetestowaną funkcją jest tworzenie szablonów, głównie dlatego, że jest to najnowsza funkcja w mojej aplikacji. Dodatkowo tak naprawdę trudnym zadaniem jest przetestowanie wystarczająco tej funkcji, gdyż jest dosyć sporo formatów faktur, szczególnie mniejsze firmy mają własne formaty faktur, które często mocno się od siebie różnią. Moją aplikację przetestowałem na około 6 różnych formatach faktur.

Aby móc korzystać z aplikacji, konieczne jest dodanie użytkownika do listy użytkowników mogących korzystać z logowania poprzez Gmail. W większości przypadków polega to na tym, że dodaje się do listy użytkowników użytkownika identyfikowanego po adresie e-mail z Gmail. W razie potrzeby skorzystania z tej aplikacji na poważnie proszę napisać wiadomość na mój adres e-mail kamdyw@wp.pl i chętnie dodam adres e-mail do listy użytkowników.

### Technologie:
* Java,
* Swing,
* Maven,
* Gmail:
    - REST,
    - JSON,
    - Spring,
    - Spring Boot,
    - JWT,
    - OAuth 2.0,
    - HttpClient (wysyłanie żądań do Api od Gmail).
* SubiektGT:
    - SubiektGT Sfera,
    - COM,
    - Php (nakładka na sferę).
* Apache PDFBox,
* Git.