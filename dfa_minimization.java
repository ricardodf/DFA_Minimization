import java.util.*;

// Representa un Estado (Q) del AFD
class Estado{
    private String id;  // Identificador String
    private int idNum;  // Identificador Numérico
    private boolean esFinal;    // 0 = No, 1 = Si
    private boolean esInicial;  // 0 = No, 1 = Si
    private int ConjuntoPerteneciente;  // Clase a que pertenece (ver más en Marcar/CrearClases)

    public void setId(String id) { this.id = id; }
    public void setIdNum(int idNum) { this.idNum = idNum; }
    public void setEsFinal(boolean esFinal) { this.esFinal = esFinal; }
    public void setEsInicial(boolean esInicial) { this.esInicial = esInicial; }
    public void setConjuntoPerteneciente(int conjunto) { this.ConjuntoPerteneciente = conjunto;}

    public String getId() { return id; }
    public int getIdNum() { return idNum; }
    public boolean getEsFinal() { return esFinal; }
    public boolean getEsInicial() { return esInicial; }
    public int getConjuntoPerteneciente() { return ConjuntoPerteneciente; }

    // Cosntructor por String
    public Estado(String id){
        setId("Q"+id);
        setIdNum(Integer.parseInt(id));
        setEsFinal(false);
        if(id.equals("0"))
            setEsInicial(true);
        else
            setEsInicial(false);
    }

    // Constructor por Integer, usado cuando se crea el nuevo AFD reducido
    public Estado(int id){
        setId(""+id);
        setIdNum(id);
        setEsFinal(false);
        if(id == 0)
            setEsInicial(true);
        else
            setEsInicial(false);
    }

    public String toString(){
        return String.format("%s", this.id);
    }
}

// Representa un par de estados (q0, q1)
class Pair{
    private Estado e1, e2;

    public void setE1(Estado e1) { this.e1 = e1; }
    public void setE2(Estado e2) { this.e2 = e2; }

    public Estado getE1() { return e1; }
    public Estado getE2() { return e2; }

    public Pair(Estado e1, Estado e2){
        setE1(e1);
        setE2(e2);
    }

    // Revisar si c/Estado del par van al mismo conjunto de clase de equivalencia
    public boolean checarEquivalencia(List<List<Estado>> transitions, HashMap<Integer,List<Estado>> conjuntos){
        Estado tmpE1, tmpE2;
        int conjuntoE1 = -1, conjuntoE2 = -1;
        boolean result[] = new boolean[transitions.get(0).size()]; //Array con resultado de equivalencias

        //Buscar en cada fila de la matriz de transiciones
        for(int i=0; i<transitions.get(0).size(); i++){
            tmpE1 = transitions.get(getE1().getIdNum()).get(i);
            tmpE2 = transitions.get(getE2().getIdNum()).get(i);
            //Buscar en el HashMap de mis clases de equivalencia actuales
            for(int j=0; j<conjuntos.size(); j++){
                if(conjuntos.get(j).contains(tmpE1)){   //Revisar si en la lista (j) existe el estado E1
                    for(int indexOfList=0; indexOfList<conjuntos.get(j).size(); indexOfList++){
                        //Si el estado dentro de "conjuntos" = al estado E1, usar el conjunto al que pertenece
                        if(conjuntos.get(j).get(indexOfList).equals(tmpE1)) conjuntoE1 = conjuntos.get(j).get(indexOfList).getConjuntoPerteneciente();
                    }
                }
                // Aqui se repite pero con E2
                if(conjuntos.get(j).contains(tmpE2)){
                    for(int indexOfList=0; indexOfList<conjuntos.get(j).size(); indexOfList++){
                        if(conjuntos.get(j).get(indexOfList).equals(tmpE2)) conjuntoE2 = conjuntos.get(j).get(indexOfList).getConjuntoPerteneciente();
                    }
                }
            }
            // Si son equivalentes entre si (0 == 0 || 1 == 1) = true
            result[i] = conjuntoE1 == conjuntoE2;
        }

        // Si en algun momento resulta que no fueron equivalentes (false), el par no es equivalente
        for(boolean b : result) if(!b) return false;
        return true;
    }

    public String toString(){
        return String.format("[%s,%s]", getE1(), getE2());
    }
}

// Representa una matrix de transiciones y sus funciones
class Transitions{
    private List<Estado> estados = new ArrayList<>();
    private List<String> alfabeto = new ArrayList<>();
    private List<List<Estado>> table = new ArrayList<>();

    public void setEstados(List<Estado> estados) { this.estados = estados; }
    public void setAlfabeto(List<String> alfabeto) { this.alfabeto = alfabeto; }

    public List<Estado> getEstados() { return estados; }
    public List<String> getAlfabeto() { return alfabeto; }
    public List<List<Estado>> getTransiciones() { return table; }

    // Cosntructor que toma el alfabeto y los estados que existan
    Transitions(List<String> alfabeto, List<Estado> estados){
        setAlfabeto(alfabeto);
        setEstados(estados);
    }

    // Crear matriz de transicciones con datos del usuario
    void createTransitions(Scanner scanner, List<Estado> ListEstados){
        for(int i=0; i < estados.size(); i++) {
            table.add(new ArrayList<>());
        }

        String inputBuffer;
        int flag, indexOfEstado = 0;

        // Inicializar cada fila de la matriz
        for(int i=0; i < estados.size(); i++) {
            table.add(new ArrayList<>());
        }

        // Crear una copia de todos los estados
        List<String> ListEstadosID = new ArrayList<>();
        for (int i=0; i<ListEstados.size(); i++){
            ListEstadosID.add(ListEstados.get(i).getId());
        }

        //Ingresando cada transición conforme al alfabeto y estados
        for(int i = 0; i<estados.size(); i++) {
            for (int j = 0; j < alfabeto.size(); j++) {
                do{
                    flag=0;
                    System.out.printf("Ingresar transicion valida (%s, %s): ", ListEstados.get(i), alfabeto.get(j));
                    inputBuffer = scanner.nextLine();
                    if(ListEstadosID.contains(inputBuffer)) {   // Si el estado existe
                        for(int k=0; k<ListEstados.size(); k++){    // Buscar el estado
                            if(ListEstadosID.get(k).equals(inputBuffer))   // Tomar el índice donde encontramos el estado
                                indexOfEstado = k;
                        }
                        // En la final (i), agregar el estado dentro de ListEstados con ayuda del indice
                        table.get(i).add(ListEstados.get(indexOfEstado));
                        flag = 1;
                    }
                }while(flag==0);
            }
        }
    }

    public String toString(){
        String result = "";
        for(int i = 0; i < table.size(); i++){
            for(int j = 0; j < table.get(i).size(); j++){
                result += table.get(i).get(j) + " ";
            }
            result += "\n";
        }
        return result;
    }
}

// Representa el procedimiento marcar para minimizar el AFD
class Marcar{
    private List<Estado> estados;
    private List<String> alfabeto;
    private List<List<Estado>> completeTransitions;

    private HashMap<Integer, List<Estado>> clasesEquivalencia = new HashMap<>();

    //Constructor que toma la matriz de transiciones
    Marcar(Transitions transitions){
        this.completeTransitions = transitions.getTransiciones();
        this.alfabeto = transitions.getAlfabeto();
        this.estados = transitions.getEstados();
    }

    // Revisar que no existan estados inaccesibles
    void revisarInaccesibles(){
        List<List<Estado>> transitionsWithoutInaccesibles = new ArrayList<>(this.completeTransitions);
        List<Estado> TranscionesOneDim_List = new ArrayList<>();
        List<Estado> estadosInaccesibles = new ArrayList<>();
        List<Estado> estadosCopy = new ArrayList<>();
        int indexOfInaccesible;

        for(int i=0; i<estados.size(); i++){
            estadosCopy.add(estados.get(i));
            if(!estados.get(i).getEsInicial()) {
                estadosInaccesibles.add(estados.get(i));
            }
        }

        for(int i=0; i<estados.size(); i++){
            for(int j=0; j<alfabeto.size(); j++){
                TranscionesOneDim_List.add(transitionsWithoutInaccesibles.get(i).get(j));
            }
        }

        for(int i=0; i<TranscionesOneDim_List.size(); i++)
            estadosInaccesibles.removeAll(Collections.singleton(TranscionesOneDim_List.get(i)));

        //System.out.println(estadosInaccesibles.toString());
        if(estadosInaccesibles.size() == 0)
            System.out.println("No hay Estados Inaccesibles!");
        else {
            System.out.printf("Estados Inaccesibles: %s\n", Arrays.toString(estadosInaccesibles.toArray()));
            for(int i=estadosInaccesibles.size()-1; i>=0; i--){
                indexOfInaccesible = estadosCopy.indexOf(estadosInaccesibles.get(i));
                transitionsWithoutInaccesibles.remove(indexOfInaccesible);
                estados.remove(indexOfInaccesible);
            }
        }
        //System.out.println(transitionsWithoutInaccesibles.toString());
    }

    // Crear pares de estados con una clase de equivalencia
    private List<Pair> crearPares(List<Estado> conjunto){
        List<Pair> totalPares = new ArrayList<>();
        for(int i=0; i<conjunto.size(); i++){
            for (int j = i + 1; j < conjunto.size(); j++)
                totalPares.add(new Pair(conjunto.get(i), conjunto.get(j)));
        }
        return totalPares;
    }

    // Hace una "Deep Copy" de un HashMap ingresado
    private HashMap<Integer, List<Estado>> copy(HashMap<Integer, List<Estado>> original) {
        HashMap<Integer, List<Estado>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<Estado>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    // Crear una nueva clase de equivalencia
    public HashMap<Integer, List<Estado>> crearClases(){
        List<Estado> estadosFinales = new ArrayList<>();
        List<Estado> estadosNoFinales = new ArrayList<>();
        List<HashMap<Integer, List<Estado>>> classHistory = new ArrayList<>();  // Lista con cada clase de equivalencuia que se cree

        // indicadores útiles para los loops
        int flag=0, position=0;

        // Llenar lista de estadosNoFinales y estadosFinales
        for(int i=0; i<estados.size(); i++){
            if(estados.get(i).getEsFinal()) {
                estados.get(i).setConjuntoPerteneciente(0);
                estadosFinales.add(estados.get(i));
            }
            else {
                estados.get(i).setConjuntoPerteneciente(1);
                estadosNoFinales.add(estados.get(i));
            }
        }

        //Clases de quivalencia iniciales: finales y no finales
        clasesEquivalencia.put(0, new ArrayList<>(estadosFinales));
        clasesEquivalencia.put(1, new ArrayList<>(estadosNoFinales));

        //Guardo esta primera clase en mi classHistory
        classHistory.add(copy(clasesEquivalencia));
        System.out.printf("%d°- Equivalencia: %s\n", position, classHistory.get(position));
        // Actualizo los ConjuntoPerteneciente de cada estado de las clases de equivalencia que se encuentren
        for(int i=0; i<classHistory.get(position).size(); i++)
            for(int j=0; j<classHistory.get(position).get(i).size(); j++)
                classHistory.get(position).get(i).get(j).setConjuntoPerteneciente(i);
        position++; //Aumento el contador para el classHistory

        // Mientras que dos clases de equivalencia no sean iguales, se continua este loop
        while(flag==0) {
            // Usar partición para revisar cada clase de equivalencia y ver si se crean nuevas
            classHistory.add(Particion(copy(clasesEquivalencia)));
            System.out.printf("%d°- Equivalencia: %s\n", position, classHistory.get(position));

            // De la nueva partición, actualizar sus ConjuntosPertenecientes
            for (int j = 0; j < classHistory.get(position).size(); j++){
                for (int k = 0; k < classHistory.get(position).get(j).size(); k++)
                    classHistory.get(position).get(j).get(k).setConjuntoPerteneciente(j);
            }
            // Si la clase anterior, es la misma que la actual, se acaba el ciclo
            if(classHistory.get(position).equals(classHistory.get(position-1))) flag=1;
            position++;
        }
        // Regreso una nueva clase de equivalencia
        return new HashMap<>(classHistory.get(position-1));
    }

    // Herramienta que divide una clase de equivalencia en más clases
    private HashMap<Integer, List<Estado>> Particion(HashMap<Integer, List<Estado>> clasesEquivalencia){
        HashMap<Integer, List<Estado>> nuevasClases = new HashMap<>();
        List<Pair> pares;   // Lista de pares creados, usando alguna clase de equivalencia

        Integer countConjuntos = 0; //Contador de cada clase nueva creada
        Estado tmpE1, tmpE2;
        Pair newPairForComparing;   //Par que se crea cuando se quiera saber si son equivalentes dos estados.
        int flagSiExiste_E1, flagSiExiste_E2, encontreConjunto, parDuplicado;   // Indicadores útiles

        // Loop que va por todas las clases que existen actualmente
        for(int i=0; i<clasesEquivalencia.size(); i++){
            // Si el conjunto es menor que 2, se agrega directamente a la nueva clase de equivalencia
            if(clasesEquivalencia.get(i).size() < 2){
                nuevasClases.put(countConjuntos, new ArrayList<>(clasesEquivalencia.get(countConjuntos)));
                for(int j=0; j<clasesEquivalencia.get(countConjuntos).size(); j++){
                    nuevasClases.get(i).get(j).setConjuntoPerteneciente(countConjuntos);
                }
                countConjuntos++;   //IMPORTANTE: Aumentamos el contador
            }

            // Si el conjunto es mayor a 2
            if(clasesEquivalencia.get(i).size() > 2){
                pares = crearPares(clasesEquivalencia.get(i));  //Creamos pares de ese conjunto
                // Por cada par creado...
                for(int indexOfPares=0; indexOfPares<pares.size(); indexOfPares++){
                    parDuplicado = 0;
                    // Checamos que c/elemento del par no exista ya en la nueva clase equivalencia
                    for(int buscadorDup=0; buscadorDup<nuevasClases.size(); buscadorDup++){
                        if(nuevasClases.get(buscadorDup).contains(pares.get(indexOfPares).getE1()) && nuevasClases.get(buscadorDup).contains(pares.get(indexOfPares).getE2()))
                            parDuplicado = 1;   // Si ya existen, nos saltamos este par
                    }
                    // Si no existe algun estado y son equivalentes
                    if(parDuplicado==0 && pares.get(indexOfPares).checarEquivalencia(completeTransitions, clasesEquivalencia)){
                        flagSiExiste_E1 = flagSiExiste_E2 = 0;
                        tmpE1 = pares.get(indexOfPares).getE1();    //Agarramos el c/estado del par
                        tmpE2 = pares.get(indexOfPares).getE2();
                        if(!nuevasClases.isEmpty()){    // Si existe alguna clase de equivalencia
                            for (int searchKey = 0; searchKey < nuevasClases.size(); searchKey++) {
                                if (nuevasClases.get(searchKey).contains(tmpE1)) {  // Si E1 existe ya en un conjunto de la nueva clase, agregamos E2 en este mismo
                                    nuevasClases.get(searchKey).add(tmpE2);
                                    flagSiExiste_E1 = 1;
                                    break;
                                }
                                if (nuevasClases.get(searchKey).contains(tmpE2)) {  // Si E2 existe ya en un conjunto de la nueva clase, agregamos E1 en este mismo
                                    nuevasClases.get(searchKey).add(tmpE1);
                                    flagSiExiste_E2 = 1;
                                    break;
                                }
                            }
                        }   // Si ninguno existe en algun conjunto de la nueva clase de equivalencia, agregamos los dos a un mismo conjunto nuevo
                        if(flagSiExiste_E1 == 0 && flagSiExiste_E2 == 0){
                            nuevasClases.put(countConjuntos, new ArrayList<>(Arrays.asList(tmpE1, tmpE2)));
                            countConjuntos++; //IMPORTANTE: Aumentamos el contador
                        }
                    }
                    // Si no es equivalente
                    else{
                        flagSiExiste_E1 = flagSiExiste_E2 = encontreConjunto = 0;
                        tmpE1 = pares.get(indexOfPares).getE1();
                        tmpE2 = pares.get(indexOfPares).getE2();
                        if(!nuevasClases.isEmpty()){    // Si ya existe alguna clase de equivalencia
                            for(int searchKey = 0; searchKey < nuevasClases.size(); searchKey++){   // Revisamos la existencia de c/estado del par
                                if (nuevasClases.get(searchKey).contains(tmpE1)){
                                    flagSiExiste_E1 = 1;
                                }
                                if (nuevasClases.get(searchKey).contains(tmpE2)){
                                    flagSiExiste_E2 = 1;
                                }
                            }
                            // Si E1 existe, pero E2 no
                            if(flagSiExiste_E1==1 && flagSiExiste_E2==0 && nuevasClases.size()>1){
                                for(int indexFindConjunto=0; indexFindConjunto<nuevasClases.size(); indexFindConjunto++){
                                    // Buscamos en c/clase nueva que existe, si E2 es equivalente con algun otro estado de esa clase
                                    // No tiene que ser un conjunto donde ya existe E1 o que sea final
                                    if(!nuevasClases.get(indexFindConjunto).contains(tmpE1) && !nuevasClases.get(indexFindConjunto).get(0).getEsFinal()){
                                        newPairForComparing = new Pair(nuevasClases.get(indexFindConjunto).get(0),tmpE2);
                                        if(newPairForComparing.checarEquivalencia(completeTransitions, clasesEquivalencia)){
                                            encontreConjunto = 1;
                                        }
                                    }   // Si lo encontramos, lo agregamos a ese conjunto
                                    if(encontreConjunto==1){
                                        nuevasClases.get(indexFindConjunto).add(tmpE2);
                                        break;
                                    }
                                }   // Si no lo encontramos, hacemos una nueva clase de equivalencia
                                if(encontreConjunto == 0){
                                    nuevasClases.put(countConjuntos, new ArrayList<>());
                                    nuevasClases.get(countConjuntos).add(tmpE2);
                                    countConjuntos++; //IMPORTANTE: Aumentamos el contador
                                }
                            }
                            // Si E2 existe, pero E1 no
                            if(flagSiExiste_E2 == 1 && flagSiExiste_E1==0 && nuevasClases.size()>1){
                                for(int indexFindConjunto=0; indexFindConjunto<nuevasClases.size(); indexFindConjunto++){
                                    // Buscamos en c/clase nueva que existe, si E1 es equivalente con algun otro estado de esa clase
                                    // No tiene que ser un conjunto donde ya existe E2 o que sea final
                                    if(!nuevasClases.get(indexFindConjunto).contains(tmpE2) && !nuevasClases.get(indexFindConjunto).get(0).getEsFinal()){
                                        newPairForComparing = new Pair(nuevasClases.get(indexFindConjunto).get(0),tmpE1);
                                        if(newPairForComparing.checarEquivalencia(completeTransitions, clasesEquivalencia)){
                                            encontreConjunto = 1;
                                        }
                                    }  // Si lo encontramos, lo agregamos a ese conjunto
                                    if(encontreConjunto==1){
                                        nuevasClases.get(indexFindConjunto).add(tmpE1);
                                        break;
                                    }
                                } // Si no lo encontramos, hacemos una nueva clase de equivalencia
                                if(encontreConjunto == 0){
                                    nuevasClases.put(countConjuntos, new ArrayList<>());
                                    nuevasClases.get(countConjuntos).add(tmpE1);
                                    countConjuntos++; //IMPORTANTE: Aumentamos el contador
                                }
                            }   // Si no existe c/Estado en ninguna nueva clase de equivalencia, creamos 2 nuevas clases para c/Estado
                            if(flagSiExiste_E1 == 0 && flagSiExiste_E2 == 0){
                                nuevasClases.put(countConjuntos, new ArrayList<>());
                                nuevasClases.get(countConjuntos).add(tmpE1);
                                countConjuntos++;
                                nuevasClases.put(countConjuntos, new ArrayList<>());
                                nuevasClases.get(countConjuntos).add(tmpE2);
                                countConjuntos++;
                            }
                        }else{  // Si no hay clases creadas, creamos 2 nuevas clases para c/Estado
                            nuevasClases.put(countConjuntos, new ArrayList<>());
                            nuevasClases.get(countConjuntos).add(tmpE1);
                            countConjuntos++;
                            nuevasClases.put(countConjuntos, new ArrayList<>());
                            nuevasClases.get(countConjuntos).add(tmpE2);
                            countConjuntos++;
                        }
                    }
                }
            }
        }
        return nuevasClases;    // Regresamos las nuevas clases
    }
}

// Representa el procedimiento reducir y sus funciones
class Reducir{
    private HashMap<Integer, List<Estado>> clasesReducidas; // Clase de equivalencia final
    private List<Estado> nuevosEstados = new ArrayList<>(); // Los nuevos estados
    private List<String> nuevosEstadosString = new ArrayList<>();   // Los nuevos estados pero solo su número
    private List<Estado> singleEstadoID = new ArrayList<>();    //Los primeros estados en c/clase de equivalencia
    private List<List<Estado>> nuevasTransiciones = new ArrayList<>();

    // Constructor que solo toma las clases de equivalencia final
    public Reducir(HashMap<Integer, List<Estado>> clasesReducidas){
        this.clasesReducidas = new HashMap<>(clasesReducidas);
    }

    // Creamos nuevos estados, usando su subíndice (Estado.getIdNum)
    public void crearNuevosEstados(){
        String newEstatoID = "";
        for(int i=0; i<clasesReducidas.size(); i++){    // Por cada clase de equivalencia
            for(int j=0; j<clasesReducidas.get(i).size(); j++) {
                newEstatoID += clasesReducidas.get(i).get(j).getIdNum();    // Unimos todos los subíndices de los estados de la clase
            }
            nuevosEstadosString.add(newEstatoID);   // Agregamos el valor a nuevosEstadosString
            nuevosEstados.add(new Estado(Integer.parseInt(newEstatoID)));   // Agregamos el estado nuevo
            if(clasesReducidas.get(i).get(0).getEsFinal()){ // Si el nuevo estado es final
                nuevosEstados.get(i).setEsFinal(true);
            }
            if(clasesReducidas.get(i).get(0).getEsInicial()){   // Si el nuevo estado es inicial
                nuevosEstados.get(i).setEsInicial(true);
            }
            singleEstadoID.add((clasesReducidas.get(i).get(0)));    // Agregamos el primer estado de cada clase a este arreglo
            newEstatoID = "";
        }
        System.out.println(nuevosEstadosString);
    }

    // Creamos nuevas transiciones con los nuevos estados y usando la anterior transición
    public void crearNuevasTransiciones(Transitions T){
        for(int i=0; i < nuevosEstados.size(); i++) {   // Inicializamos la matriz
            nuevasTransiciones.add(new ArrayList<>());
        }
        Estado tmpE;
        for(int i=0; i<nuevosEstados.size(); i++){
            for(int j=0; j<T.getAlfabeto().size(); j++){
                // Usamos singleEstadoID para sacar el estado dentro de la antigua transición
                tmpE = T.getTransiciones().get(singleEstadoID.get(i).getIdNum()).get(j);
                nuevasTransiciones.get(i).add(tmpE);    //  Agregamos ese estado como referencia para la nueva matriz
            }
        }
    }

    // Imprimimos en pantalla el AFD Minimizado
    public void printResults(Transitions T){
        String idNumValue;
        for(int i=0; i<nuevasTransiciones.size(); i++){
            for(int j=0; j<nuevasTransiciones.get(i).size(); j++){
                for(int k=0; k<singleEstadoID.size(); k++){
                    idNumValue = ""+nuevasTransiciones.get(i).get(j).getIdNum();    //Buscamos el estado en la nueva matriz
                    if(nuevosEstadosString.get(k).contains(idNumValue)){    //Revisamos que el idNum coincida con algun estado en nuevosEstadosString
                        if(nuevosEstados.get(i).getEsInicial()) // Formato de impresión de un estado inicial
                            System.out.printf("[I]:    %s --(%s)--> %s\n", nuevosEstadosString.get(i), T.getAlfabeto().get(j), nuevosEstadosString.get(k));
                        else if(nuevosEstados.get(i).getEsFinal()) // Formato de impresión de un estado final
                            System.out.printf("[F]:    %s --(%s)--> %s\n", nuevosEstadosString.get(i), T.getAlfabeto().get(j), nuevosEstadosString.get(k));
                        else if(nuevosEstados.get(i).getEsInicial() && nuevosEstados.get(i).getEsFinal()) // Formato de impresión de un estado inicial y final
                            System.out.printf("[I][F]: %s --(%s)--> %s\n", nuevosEstadosString.get(i), T.getAlfabeto().get(j), nuevosEstadosString.get(k));
                        else { // Formato de impresión de un estado normal
                            System.out.printf("        %s --(%s)--> %s\n", nuevosEstadosString.get(i), T.getAlfabeto().get(j), nuevosEstadosString.get(k));
                        }
                    }
                }
            }
        }
    }
}

// Junta las clases Marcar y Reducir para resolver el AFD
class Minimizar{
    private Transitions T;
    private HashMap<Integer, List<Estado>> clasesReducidas;

    // Solo toma la matriz de transiciones inicial
    public Minimizar(Transitions T){
        this.T = T;
    }

    public void procedimientoMarcar(){
        System.out.println("1) Revisar estados inaccesibles");
        Marcar M = new Marcar(T);
        M.revisarInaccesibles();
        System.out.println("\n2) Crear clases de equivalencia");
        clasesReducidas = new HashMap<>(M.crearClases());
        System.out.printf("Clases de Equivalencia Finales: %s\n", clasesReducidas);
    }

    public void procedimientoReducir(){
        Reducir R = new Reducir(clasesReducidas);
        System.out.println("\n3) Nuevos Estados:");
        R.crearNuevosEstados();
        System.out.println("\nCreando nuevas transiciones...");
        R.crearNuevasTransiciones(T);

        System.out.println("\nAFD Reducido:");
        R.printResults(T);
    }
}

public class dfa_minimization {
    public static void main(String[] args){
        Scanner inputScanner = new Scanner(System.in);  // Scanner para recibir datos del usuario
        String inputBuffer; //Recibe el dato de entrada sin limpiar
        List<String> alfabeto = new ArrayList<>(), estadosFinales;
        int numEstados;

        // Recibir el alfabeto a usar
        System.out.print("Ingresar alfabeto (cada elemento dividido por comas y/o espacios): ");
        inputBuffer = inputScanner.nextLine();
        alfabeto = Arrays.asList(inputBuffer.split("\\s*(,|\\s)\\s*")); //De la entrada, quita cualquier espacio y/o coma

        // Recibir el número de estados a usar
        System.out.print("Ingresar número total de estados: ");
        inputBuffer = inputScanner.nextLine();
        numEstados = Integer.parseInt(inputBuffer);

        // Creamos los estados a usar y los incluimos en estadosActuales
        List<Estado> estadosActuales = new ArrayList<>();
        for(int i = 0; i<numEstados; i++)
            estadosActuales.add(new Estado(""+i));

        int flag;
        do{ // Ciclo que pide los estados finales, checa si existen dentro de los estadosActuales
            flag = 0;
            System.out.printf("Indicar del estado Q%d al Q%d, cuales son finales (cada elemento dividido por comas y/o espacios): ", 0, numEstados-1);
            inputBuffer = inputScanner.nextLine();
            estadosFinales = Arrays.asList(inputBuffer.split("\\s*(,|\\s)\\s*")); //De la entrada, quita cualquier espacio y/o coma

            int flagForEstado = 0;
            for(String estado : estadosFinales) {
                flagForEstado = 0;
                for(int i = 0; i<numEstados; i++) {
                    if (estadosActuales.get(i).getId().equals(estado)) {
                        estadosActuales.get(i).setEsFinal(true);
                        flagForEstado = 1;
                    }
                }
                if(flagForEstado == 0) break;
            }
            if(flagForEstado == 1) flag = 1;
        }while(flag==0);

        // Creamos la matriz de equivalencia
        Transitions T = new Transitions(alfabeto, estadosActuales);
        System.out.println("\nCrear matriz de transiciones...");
        T.createTransitions(inputScanner, estadosActuales);

        // Resolvemos el AFD
        System.out.println("\nIniciando minimización...\n");
        Minimizar M = new Minimizar(T);
        M.procedimientoMarcar();
        M.procedimientoReducir();

        // Cerramos el Scanner
        inputScanner.close();
    }
}
