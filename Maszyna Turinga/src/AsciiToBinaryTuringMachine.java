import java.util.*; // Importuje wszystkie klasy z pakietu java.util (List, Map, Scanner itd.)

public class AsciiToBinaryTuringMachine { // Definicja klasy reprezentującej maszynę Turinga konwertującą znak ASCII na binarny

    private final List<Character> tape; // Lista reprezentująca taśmę maszyny (8 komórek)
    private int headPosition; // Pozycja głowicy na taśmie
    private String currentState; // Aktualny stan maszyny Turinga
    private final Map<String, Transition> transitionTable; // Mapa przejść: klucz = stan + symbol, wartość = przejście
    private final String finalState; // Stan końcowy maszyny

    public AsciiToBinaryTuringMachine(char inputChar) { // Konstruktor klasy, przyjmuje znak do zakodowania
        this.tape = new ArrayList<>(Collections.nCopies(8, '_')); // Inicjalizuje taśmę z 8 pustymi komórkami ('_')
        this.headPosition = 0; // Głowica zaczyna na pierwszej pozycji
        this.currentState = "q0"; // Początkowy stan maszyny
        this.transitionTable = new HashMap<>(); // Tworzy pustą tabelę przejść
        this.finalState = "q8"; // Definiuje stan końcowy jako "q8"

        String binary = String.format("%8s", Integer.toBinaryString(inputChar)).replace(' ', '0'); // Konwertuje znak na 8-bitowy ciąg binarny
        System.out.println("Kod binarny znaku '" + inputChar + "' to: " + binary); // Wyświetla binarny kod znaku

        // Dodaje przejścia do tabeli – jedno dla każdego bitu (stan q0 do q8)
        for (int i = 0; i < 8; i++) {
            String state = "q" + i; // Bieżący stan
            String nextState = "q" + (i + 1); // Następny stan
            char bit = binary.charAt(i); // Bit do zapisania na taśmie

            transitionTable.put(state + "_", new Transition(bit, 'R', nextState)); // Dodaje przejście: z '_' zapisuje bit, przesuwa się w prawo, przechodzi do następnego stanu
        }

        // Dodaje ostatnie przejście do stanu końcowego (pozostaje w miejscu)
        transitionTable.put("q8_", new Transition('_', 'N', "q8"));
    }

    public void run() { // Metoda uruchamiająca maszynę Turinga
        // Wyświetla nagłówki tabeli logów
        System.out.printf("%-30s %-10s %-10s %-20s %-10s %-12s\n",
                "Taśma z głowicą", "Odczyt", "Stan", "Operacja", "Zapis", "Nowy stan");

        // Dopóki nie osiągnięto stanu końcowego
        while (!currentState.equals(finalState)) {
            char currentSymbol = tape.get(headPosition); // Pobiera aktualny symbol z taśmy
            String key = currentState + currentSymbol; // Tworzy klucz do wyszukania w tabeli przejść
            Transition t = transitionTable.get(key); // Pobiera przejście z tabeli

            if (t == null) { // Jeśli brak przejścia
                System.out.printf("%-30s %-10s %-10s %-20s %-10s %-12s\n",
                        displayTapeWithHead(), currentSymbol, currentState,
                        "brak instrukcji", "", ""); // Wyświetla informację o braku przejścia
                break; // Zatrzymuje działanie maszyny
            }

            // Wyświetla szczegóły przejścia (zapis, ruch, nowy stan)
            System.out.printf("%-30s %-10s %-10s %-20s %-10s %-12s\n",
                    displayTapeWithHead(),
                    currentSymbol,
                    currentState,
                    String.format("%c,%s,%c", t.write, t.nextState, t.move),
                    t.write,
                    t.nextState);

            tape.set(headPosition, t.write); // Zapisuje symbol na taśmie
            if (t.move == 'R') headPosition++; // Jeśli ruch w prawo – przesuwa głowicę
            else if (t.move == 'L') headPosition--; // Jeśli ruch w lewo – przesuwa głowicę w lewo
            currentState = t.nextState; // Ustawia nowy stan
        }

        System.out.println("\nWynik końcowy (taśma): " + tapeToString()); // Wyświetla zawartość taśmy po zakończeniu działania
    }

    private String displayTapeWithHead() { // Zwraca ciąg znaków reprezentujący taśmę z zaznaczoną pozycją głowicy
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tape.size(); i++) {
            if (i == headPosition) {
                sb.append("[").append(tape.get(i)).append("]"); // Zaznacza głowicę nawiasami []
            } else {
                sb.append(" ").append(tape.get(i)).append(" "); // Pozostałe komórki
            }
        }
        return sb.toString(); // Zwraca wizualizację taśmy
    }

    private String tapeToString() { // Zwraca zawartość taśmy jako zwykły ciąg znaków
        StringBuilder sb = new StringBuilder();
        for (char c : tape) {
            sb.append(c); // Dodaje każdy znak z taśmy
        }
        return sb.toString(); // Zwraca zawartość taśmy
    }

    private static class Transition { // Klasa wewnętrzna reprezentująca przejście w maszynie Turinga
        char write; // Symbol do zapisania
        char move; // Kierunek ruchu ('R', 'L', 'N')
        String nextState; // Następny stan

        Transition(char write, char move, String nextState) { // Konstruktor przejścia
            this.write = write;
            this.move = move;
            this.nextState = nextState;
        }
    }

    public static void main(String[] args) { // Metoda główna – uruchamia program
        System.out.println("Copyright ©");
        System.out.println("Krystian Zatka, 124048");
        Scanner scanner = new Scanner(System.in); // Tworzy skaner do wczytywania danych z konsoli
        System.out.print("Podaj jeden znak do zakodowania: "); // Prosi użytkownika o znak
        String input = scanner.nextLine(); // Wczytuje cały wiersz

        if (input.isEmpty()) { // Sprawdza, czy coś podano
            System.out.println("Nie podano znaku."); // Jeśli nie – informuje
            return; // Kończy działanie programu
        }

        char inputChar = input.charAt(0); // Pobiera pierwszy znak z wejścia

        AsciiToBinaryTuringMachine machine = new AsciiToBinaryTuringMachine(inputChar); // Tworzy maszynę Turinga z podanym znakiem
        machine.run(); // Uruchamia maszynę
    }
}
