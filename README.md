Aplikacja została zaprojektowana jako kompleksowy system wspierający zarządzanie danymi cmentarzy na terenie Gdańska. Umożliwia efektywne gromadzenie, przetwarzanie i raportowanie informacji dotyczących grobów oraz osób zmarłych.

System umożliwia wyszukiwanie informacji według kluczowego kryterium, jakim jest lokalizacja grobu, z podziałem wyników na dwa odrębne widoki: Zmarli oraz Groby. Dane zmarłych są automatycznie klasyfikowane na osoby z przypisanym grobem i bez przypisanego grobu, co pozwala na szybką identyfikację brakujących powiązań i ułatwia porządkowanie danych.

W ramach modułów edycji dostępne są zaawansowane funkcje umożliwiające:
- modyfikację danych osobowych i grobu,
- niezależną edycję lokalizacji zmarłego oraz daty opłacenia za grób,
- przeglądanie szczegółów grobu z możliwością przypisywania istniejących lub nowych osób zmarłych,
- dodawanie nowych grobów do systemu.

Dodatkowym atutem aplikacji jest generowanie raportów PDF, pozwalających na eksport danych dotyczących:
- listy grobów wyszukanych według lokalizacji,
- szczegółowych informacji o wybranym grobie wraz z powiązanymi osobami.

Aplikacja jest zrealizowana w JavaFX w formie desktopowej w architekturze modularnej z podziałem na backend i frontend.

Każdy z cmentarzy obsługiwanych przez system posiada niezależną bazę danych, z którą aplikacja łączy się dynamicznie po wyborze cmentarza podczas uruchamiania.
Po nawiązaniu połączenia klient utrzymuje jedno aktywne połączenie z bazą danych przez cały czas działania instancji aplikacji, co minimalizuje obciążenie serwera i zapewnia stabilność działania. Po zamknięciu aplikacji połączenie jest automatycznie zwalniane.

Raporty są generowane na podstawie szablonu przygotowanego w JasperReports.

Warstwa danych oparta jest na systemie PostgreSQL, zlokalizowanym na zdalnym serwerze dostępnym poprzez bezpieczny kanał VPN. Rozwiązanie to umożliwia równoczesną pracę wielu użytkowników w różnych lokalizacjach.
