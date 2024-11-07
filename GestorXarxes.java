/** @file GestorXarxes.java
    @brief Classe GestorXarxes
*/

/** @class GestorXarxes
    @brief Encarregat de fer determinades operacions osbre una Xarxa d'aigua
    @author Marcel·lí Corominas
*/

import java.util.*;

public class GestorXarxes {

    public GestorXarxes() {}


    /** @brief Diu si la component connexa de la xarxa x que conté nodeOrigen té cicles
	@pre  nodeOrigen pertany a la xarxa x 
	@post Retorna true si la component connexa de la xarxa x que conté nodeOrigen té cicles
    */
    public static boolean teCicles(Xarxa x, Origen nodeOrigen) {
        if (x == null || nodeOrigen == null) {
            System.out.println("Error amb les dades");
        }
        else {
            Set<Node> visitats = new HashSet<>();
            Map<Node, Node> pare = new HashMap<>();
            Queue<Node> cua = new LinkedList<>();
            cua.offer(nodeOrigen);
            visitats.add(nodeOrigen);

            while (!cua.isEmpty()) {
                Node nodeActual = cua.poll();
                Iterator<Canonada> veins = x.sortides(nodeActual);
                while (veins != null && veins.hasNext()) {
                    Canonada canonada = veins.next();
                    Node desti = canonada.node2();
                    if (visitats.contains(desti) && pare.get(nodeActual) != desti) {
                        return true;
                    }
                    if (!visitats.contains(desti)) {
                        cua.offer(desti);
                        visitats.add(desti);
                        pare.put(desti, nodeActual);
                    }
                }
            }
        }
        return false;
    }

    /** @brief Diu si la component connexa de la xarxa x que conté nodeOrigen és un arbre
	@pre   nodeOrigen pertany a la xarxa x
	@post Retorna true si la component connexa de la xarxa x que conté nodeOrigen és un arbre
    */
    public static boolean esArbre(Xarxa x, Origen nodeOrigen) {
        if (teCicles(x, nodeOrigen)) {
            return false;
        }
        Map<Node, Integer> arestes = new HashMap<>();
        Set<Node> visitats = new HashSet<>();
        visitats.add(nodeOrigen);
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(nodeOrigen);

        while (!nodes.isEmpty()) {
            Node actual = nodes.remove();
            Iterator<Canonada> sortides = x.sortides(actual);

            if (sortides != null) {
                while (sortides.hasNext()) {
                    Canonada can = sortides.next();
                    Node dest = can.node2();
                    arestes.put(dest, arestes.getOrDefault(dest, 0) + 1);

                    if (!visitats.contains(dest)) {
                        visitats.add(dest);
                        nodes.add(dest);
                    }
                }
            }
        }

        for (Map.Entry<Node, Integer> entrada : arestes.entrySet()) {
            if (entrada.getKey().equals(nodeOrigen)) {
                if (entrada.getValue() != 0)
                    return false;
            } else {
                if (entrada.getValue() != 1)
                    return false;
            }
        }
        return true;
    }

    /** @brief Diu el cabal mínim que hi ha d'haver entre tots els origens perquè s'assoleixi el tant per cent de la demanda total.
	@pre  nodeOrigen pertany a la xarxa x, la component connexa de la xarxa x que conté nodeOrigen no té cicles,
          i percentatgeDemandaSatisfet > 0 
	@post Retorna el cabal mínim que hi hauria d’haver entre tots els nodes d’origen de la component connexa
          de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells
          on arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet% de la seva demanda
    */
    public static float cabalMinim(Xarxa x, Origen nodeOrigen, float percentatgeDemandaSatisfet) {
        if (!esArbre(x, nodeOrigen)) {
            System.out.println("La xarxa té cicles o no és un arbre");
        }
        else {
            Set<Node> origens = x.origensXarxa(nodeOrigen);
            Map<Terminal, Float> cabalsMinims = new HashMap<>();
            Queue<Node> nodes = new LinkedList<>();
            for (Node origen : origens) {
                if (origen.aixetaOberta()) {
                    nodes.add(origen);
                }
            }

            while (!nodes.isEmpty()) {
                Node actual = nodes.poll();
                Iterator<Canonada> sortides = x.sortides(actual);

                while (sortides != null && sortides.hasNext()) {
                    Canonada can = sortides.next();
                    Node desti = can.node2();
                    if (desti.aixetaOberta() && !nodes.contains(desti)) {
                        if (desti instanceof Terminal) {

                            Terminal ter = (Terminal) desti;
                            float demandaActual = ter.demandaActual();
                            float demandaMinima = demandaActual * (percentatgeDemandaSatisfet / 100);
                            cabalsMinims.put(ter, demandaMinima);

                        } else {
                            nodes.add(desti);
                        }
                    }
                }
            }
            float cabalTotalMinim = 0;
            for (Float cabal : cabalsMinims.values()) {
                cabalTotalMinim += cabal;
            }
            return cabalTotalMinim;
        }
        return 0;
    }

    /** @brief Crea un conjunt dels Terminals que es poden accedir desde inici
	@pre  inici pertany a la xarxa x 
	@post Retorna una llista amb tots els terminals accessibles des del node inici
    */
    private static List<Terminal> terminals(Node inici, Xarxa x) {
        if (inici == null) {
            return new ArrayList<>();
        }
        List<Terminal> terminals = new ArrayList<>();
        List<Node> visitats = new ArrayList<>();
        Queue<Node> cua = new LinkedList<>();
        cua.add(inici);
        if (inici instanceof Terminal) {
            terminals.add((Terminal) inici);
        }
        while (!cua.isEmpty()) {
            Node current = cua.poll();
            visitats.add(current);

            processarSortides(current, x, terminals, visitats, cua);
            processarEntrades(current, x, terminals, visitats, cua);
        }
        return terminals;
    }

    /** @brief Processa les sortides del node current, afegint els nodes terminals a la llista terminals i els nodes visitats a la cua
	@pre  current, x, terminals, visitats i cua no són nulls 
	@post Processa les sortides del node current, afegint els nodes terminals a la llista terminals i els nodes visitats a la cua
    */
    private static void processarSortides(Node current, Xarxa x, List<Terminal> terminals, List<Node> visitats, Queue<Node> cua) {
        Iterator<Canonada> sortides = x.sortides(current);
        while (sortides != null && sortides.hasNext()) {
            Node actual = sortides.next().node2();
            if (actual instanceof Terminal) {
                if (!terminals.contains((Terminal) actual)) {
                    terminals.add((Terminal) actual);
                }
            }
            if (!visitats.contains(actual)) {
                cua.add(actual);
            }
        }
    }

    /** @brief Processa les entrades del node current, afegint els nodes terminals a la llista terminals i els nodes visitats a la cua
	@pre  current, x, terminals, visitats i cua no són nulls 
	@post Processa les entrades del node current, afegint els nodes terminals a la llista terminals i els nodes visitats a la cua
    */
    private static void processarEntrades(Node current, Xarxa x, List<Terminal> terminals, List<Node> visitats, Queue<Node> cua) {
        Iterator<Canonada> entrades = x.entrades(current);
        while (entrades != null && entrades.hasNext()) {
            Node actual = entrades.next().node1();
            if (actual instanceof Terminal) {
                if (!terminals.contains((Terminal) actual)) {
                    terminals.add((Terminal) actual);
                }
            }
            if (!visitats.contains(actual)) {
                cua.add(actual);
            }
        }
    }

   /** @brief Trobar un origen de la xarxa
    @pre  Node n pertany a la xarxa
    @post   Retorna un origen de la xarxa que conté el node n
    */
private static Origen trobarOrigen(Node n, Xarxa x) {
    if (n instanceof Origen) {
        return (Origen) n;
    }
    Iterator<Canonada> canonadesEntrants = x.entrades(n);
    while (canonadesEntrants != null && canonadesEntrants.hasNext()) {
        Canonada canonada = canonadesEntrants.next();
        Node nodeAnterior = canonada.node1();
        Origen origenTrobat = trobarOrigen(nodeAnterior, x);
        if (origenTrobat != null) {
            return origenTrobat;
        }
    }
    return null;
}
/** @brief Retorna les canonades entrades que tenen un exces de cabal
 @pre  origen pertany a la xarxa x i Set<Canonada> canonades no és buit
 @post   Retorna totes les canonades del Set<Canonada> canonades que Tenen un excés de cabal si repartim el cabal proporcional a la demanda dels temrinals.
 */
public static Set<Canonada> excesCabal(Xarxa x, Set<Canonada> canonades) {
    Set<Canonada> can=canonades;
    Canonada c1=can.iterator().next();
    Origen origen=trobarOrigen(c1.node1(),x);
    Set<Canonada> canonadesExces = new HashSet<>();
    if (teCicles(x, origen)) {
        System.out.println("La xarxa té cicles o no és un arbre");
    }
    else {

        Map<Node, Float> demandaAcumulada = calcularDemandaAcumulada(x, origen);
        Map<Canonada, Float> cabalCanonades = calcularCabalCanonades(x, origen, demandaAcumulada);
        for (Canonada canonada : canonades) {
            float capacitat = canonada.capacitat();
            float cabal = cabalCanonades.getOrDefault(canonada, 0f);
            if (cabal > capacitat) {
                canonadesExces.add(canonada);
            }
        }

    }
    return canonadesExces;
}

/** @brief Calcula la demanda acumulada de la xarxa x a partir del node d'origen
 @pre  origen pertany a la xarxa x
 @post  Retorna un mapa amb la demanda acumulada per cada node de la xarxa x
 */
private static Map<Node, Float> calcularDemandaAcumulada(Xarxa x, Origen origen) {
    Map<Node, Float> demandaAcumulada = new HashMap<>();

    for (Terminal term : terminals(origen, x)) {
        float demandaTotal = term.demandaActual();
        float totalCapacitat = calcularTotalCapacitat(x.entrades(term));
        Iterator<Canonada> canonadesEntrants = x.entrades(term);
        demandaAcumulada.merge(term, demandaTotal, Float::sum);
        while (canonadesEntrants != null && canonadesEntrants.hasNext()) {
            Canonada canonada = canonadesEntrants.next();
            Node nodeOrigen = canonada.node1();
            if (nodeOrigen.aixetaOberta()) {
                float part = canonada.capacitat() / totalCapacitat;
                float valor = demandaTotal * part;
                demandaAcumulada.merge(nodeOrigen, valor, Float::sum);
                propagarDemandaAcumulada(x, demandaAcumulada, nodeOrigen, valor);
            }
        }
    }
    return demandaAcumulada;
}

    /** @brief   Propaga la demanda acumulada pels nodes de la xarxa
     @pre  ---
     @post  Propaga la demanda acumulada pels nodes de la xarxa. Ho fa proporcional a la capacitat.
     */
    private static void propagarDemandaAcumulada(Xarxa x, Map<Node, Float> demandaAcumulada, Node origen, float demanda) {
        Iterator<Canonada> canonadesEntrants = x.entrades(origen);
        float demandaTotal = demanda;
        float totalCapacitat = calcularTotalCapacitat(x.entrades(origen));
        while (canonadesEntrants != null && canonadesEntrants.hasNext()) {
            Canonada canonada = canonadesEntrants.next();
            Node actual = canonada.node1();
            if (actual.aixetaOberta()) {
                float part = canonada.capacitat() / totalCapacitat;
                float valor = demandaTotal * part;
                demandaAcumulada.merge(actual, valor, Float::sum);
                propagarDemandaAcumulada(x, demandaAcumulada, actual, valor);
            }
        }
    }

    /** @brief  Calcula la capacitat total de les canonades
     @pre  ---
     @post  Retorna un float que representa la suma de totes les capacitats de les canonades entrades
     */
    private static float calcularTotalCapacitat(Iterator<Canonada> canonades) {
        float totalCapacitat = 0;
        while (canonades != null && canonades.hasNext()) {
            Canonada canonadaActual = canonades.next();
            if (canonadaActual.node1().aixetaOberta()) {
                totalCapacitat += canonadaActual.capacitat();
            }
        }
        return totalCapacitat;
    }



    /** @brief  Calcula la demanda total a partir de les canonades entrades
     @pre  ---
     @post  Retorna la suma de totes les demandes de les canonades entrades
     */
    private static float calcularTotalDemanda(Iterator<Canonada> canonades, Map<Node, Float> demandaAcumulada) {
        float totalDemanda = 0;
        while (canonades != null && canonades.hasNext()) {
            Canonada canonadaActual = canonades.next();
            Node nodeDesti = canonadaActual.node2();
            if (nodeDesti.aixetaOberta()) {
                if (demandaAcumulada.containsKey(nodeDesti)) {
                    totalDemanda += demandaAcumulada.get(nodeDesti);
                }
            }
        }
        return totalDemanda;
    }

    /** @brief  Calcula el cabal que passa per cada canonada
     @pre  ---
     @post  Retorna un mapa amb el cabal que passa per cada canonada
     */
    private static Map<Canonada, Float> calcularCabalCanonades(Xarxa x, Origen origen, Map<Node, Float> demandaAcumulada) {
        Map<Canonada, Float> cabalCanonades = new HashMap<>();
        Queue<Node> cua = new LinkedList<>();

        for (Node ori : x.origensXarxa(origen)) {
            Origen o = (Origen) ori;
            float cabalTotal = o.cabal();
            float totalDemanda = calcularTotalDemanda(x.sortides(o), demandaAcumulada);
            Iterator<Canonada> canonadesSortides = x.sortides(o);

            while (canonadesSortides != null && canonadesSortides.hasNext()) {
                Canonada canonada = canonadesSortides.next();
                Node nodeOrigen = canonada.node2();
                if (nodeOrigen.aixetaOberta()) {
                    if (!cua.contains(nodeOrigen)) {
                        cua.add(nodeOrigen);
                    }
                    Float demandaOrigen = demandaAcumulada.get(nodeOrigen);
                    if (demandaOrigen != null && totalDemanda != 0) {
                        float part = demandaOrigen / totalDemanda;
                        float valor = cabalTotal * part;
                        cabalCanonades.merge(canonada, valor, Float::sum);
                        propagarCabalCanonades(x, cabalCanonades, demandaAcumulada, nodeOrigen, valor);
                    }
                }
            }
        }
        return cabalCanonades;
    }

    /** @brief  Propaga el cabal per cada canonada
     @pre  ---
     @post  Propaga el cabal per cada canonada proporcional a la demanda
     */
    private static void propagarCabalCanonades(Xarxa x, Map<Canonada, Float> cabalCanonades, Map<Node, Float> demandaAcumulada, Node origen, float cabal) {
        Iterator<Canonada> canonadesSortides = x.sortides(origen);
        float cabalTotal = cabal;
        float totalDemanda = calcularTotalDemanda(x.sortides(origen), demandaAcumulada);

        while (canonadesSortides != null && canonadesSortides.hasNext()) {
            Canonada canonada = canonadesSortides.next();
            Node actual = canonada.node2();
            if (actual.aixetaOberta()) {
                Float demandaNode = demandaAcumulada.get(actual);
                if (demandaNode != null && totalDemanda != 0) {
                    float part = demandaNode / totalDemanda;
                    float valor = cabalTotal * part;
                    cabalCanonades.merge(canonada, valor, Float::sum);
                    propagarCabalCanonades(x, cabalCanonades, demandaAcumulada, actual, valor);
                }
            }
        }
    }


/** @brief Retorna el conjunt de nodes n de la xarxa x més propers (seguint la topologia) als terminals t de aiguaArriba, tals que per sota de n la situació actual de la xarxa és incoherent amb aiguaArriba
	@pre  Tots els terminals de aiguaArriba pertanyen a la xarxa x, aiguaArriba.get(t) indica si arriba aigua a t, i la xarxa x té forma d'arbre 
	@post Retorna el conjunt de nodes n de la xarxa x més propers (seguint la topologia) als terminals t de aiguaArriba, tals que per sota de n la situació actual de la xarxa és incoherent amb aiguaArriba
    */
public static Set<Node> aixetesTancar(Xarxa x, Map<Terminal, Boolean> aiguaArriba) {
    Iterator<Terminal> iterator = aiguaArriba.keySet().iterator();
    Node n = iterator.next();
    Origen origen = trobarOrigen(n, x);
    if (teCicles(x, origen)) {
        System.out.println("La xarxa no és un arbre");
    }
    else {
        Set<Node> resultatsPotencials = new HashSet<>();
        for (Map.Entry<Terminal, Boolean> terminal : aiguaArriba.entrySet()) {
            Terminal term = terminal.getKey();
            boolean teAigua = terminal.getValue();

            if (!teAigua) {
                Node actual = term;

                while (actual != null) {
                    Iterator<Canonada> itCanonadesEntrada = x.entrades(actual);
                    if (itCanonadesEntrada != null && itCanonadesEntrada.hasNext()) {
                        Canonada canonada = itCanonadesEntrada.next();
                        Node oriCan = canonada.node1();

                        if (afecta(x, oriCan, aiguaArriba)) {
                            resultatsPotencials.add(oriCan);
                        }
                        actual = oriCan;
                    } else {
                        break;
                    }
                }
            }
        }

        Set<Node> resultatFinal = new HashSet<>(resultatsPotencials);
        for (Node node : resultatsPotencials) {
            Iterator<Canonada> itCanonadesSortida = x.sortides(node);
            while (itCanonadesSortida != null && itCanonadesSortida.hasNext()) {
                Canonada canonada = itCanonadesSortida.next();
                Node desti = canonada.node2();
                if (resultatsPotencials.contains(desti)) {
                    resultatFinal.remove(node);
                    break;
                }
            }
        }

        return resultatFinal;
    }
    Set<Node> r=new HashSet<>();
    return  r;
}

    /** @brief Diu si la situació actual de la xarxa és incoherent amb aiguaArriba per sota de node
     @pre  node pertany a la xarxa x
     @post Retorna true si la situació actual de la xarxa és incoherent amb aiguaArriba per sota de node
     */
    private static boolean afecta(Xarxa x, Node node, Map<Terminal, Boolean> aiguaArriba) {
        Set<Node> visitats = new HashSet<>();
        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            Node actual = stack.pop();
            if (visitats.contains(actual)) continue;
            visitats.add(actual);

            Iterator<Canonada> sortides = x.sortides(actual);
            while (sortides != null && sortides.hasNext()) {
                Canonada canonada = sortides.next();
                Node desti = canonada.node2();
                if (desti instanceof Terminal && aiguaArriba.getOrDefault(desti, true)) {
                    return true;
                }
                if (!visitats.contains(desti)) {
                    stack.push(desti);
                }
            }
        }
        return false;
    }



    /** @brief Retorna la llista de nodes ordenats per distancia en una coordenada en concret
	@pre  --- 
	@post Retorna una llista amb els nodes de cjtNodes ordenats segons la seva distància a c i, en cas d'empat, en ordre alfabètic dels seus identificadors
    */
    public static List<Node> proximitat(Coordenades c, Set<Node> cjtNodes) {

        List<Node> resultat = new ArrayList<>(cjtNodes);
        for (int i = 0; i < resultat.size() - 1; i++) {
            int iMin = i;
            for (int j = i + 1; j < resultat.size(); j++) {
                Node actual = resultat.get(j);
                Node nMin = resultat.get(iMin);

                double distActual = actual.coordenades().distancia(c);
                double distNMin = nMin.coordenades().distancia(c);

                if (distActual < distNMin || (distActual == distNMin && actual.id().compareTo(nMin.id()) < 0)) {
                    iMin = j;
                }
            }
            Node tmp = resultat.get(i);
            resultat.set(i, resultat.get(iMin));
            resultat.set(iMin, tmp);
        }
        return resultat;
    }

    /** @brief Crea un graf residual
	@pre  nodeOrigen pertany a la xarxa x 
	@post Construeix el graf residual de la xarxa x
    */
    private static void construirGrafResidual(Xarxa x, Map<Node, List<Canonada>> grafResidual, Origen superOrigen, Terminal superTerminal, Origen nodeOrigen) {
        Set<Node> origens = x.origensXarxa(nodeOrigen);
        Queue<Node> cua = new LinkedList<>();
        Queue<Node> visitats = new LinkedList<>();
        for (Node origen : origens) {
            Iterator<Canonada> sortides = x.sortides(origen);
            while (sortides != null && sortides.hasNext()) {
                Canonada canonada = sortides.next();
                Node desti = canonada.node2();
                afegirCanonadaResidual(grafResidual, superOrigen, desti, canonada.capacitatTemporal());
                if (!cua.contains(desti)) {
                    cua.add(desti);
                }
            }
        }
        while (!cua.isEmpty()) {
            Node actual = cua.poll();
            Iterator<Canonada> sortides = x.sortides(actual);
            while (sortides != null && sortides.hasNext()) {
                visitats.add(actual);
                Canonada canonada = sortides.next();
                Node desti = canonada.node2();
                if (!visitats.contains(desti)) {
                    if (!cua.contains(desti)) {
                        cua.add(desti);
                    }
                    if (desti instanceof Terminal) {
                        afegirCanonadaResidual(grafResidual, actual, superTerminal, canonada.capacitatTemporal());
                    } else {
                        afegirCanonadaResidual(grafResidual, actual, desti, canonada.capacitatTemporal());
                    }
                }
            }
        }
    }

    /** @brief Calcula el flux màxim de la xarxa x a partir del node d'origen
	@pre   nodeOrigen pertany a la xarxa x 
	@post Calcula el flux màxim de la xarxa x a partir del node d'origen
    */
    public static void fluxMaxim(Xarxa x, Origen nodeOrigen) {

        Map<Node, List<Canonada>> grafResidual = new HashMap<>();
        List<Canonada> cami;
        Origen superOrigen = new Origen("superOrigen", new Coordenades("0:0:0N,0:0:0E"));
        Terminal superTerminal = new Terminal("superTerminal", new Coordenades("0:0:10N,0:0:10E"), Float.MAX_VALUE);
        construirGrafResidual(x, grafResidual, superOrigen, superTerminal, nodeOrigen);

        while ((cami = bfs(superOrigen, superTerminal, grafResidual)) != null) {
            float camiFlow = Float.MAX_VALUE;
            for (Canonada canonada : cami) {
                camiFlow = Math.min(camiFlow, canonada.capacitatTemporal());
            }

            for (Canonada canonada : cami) {
                Node u = canonada.node1();
                Node v = canonada.node2();
                canonadaReduirCapacitat(grafResidual, u, v, camiFlow);
            }
        }

        construirXarxaResidual(grafResidual);
    }

    /** @brief Dona un posible cami del graf residual desde un origen per arribar a terminal
	@pre  origen i terminal pertanyen a grafResidual 
	@post Retorna un camí del graf residual des d'origen fins a terminal
    */
    private static List<Canonada> bfs(Node origen, Node terminal, Map<Node, List<Canonada>> grafResidual) {

        Map<Node, Canonada> cami = new HashMap<>();
        Set<Node> visitats = new HashSet<>();
        Queue<Node> cua = new LinkedList<>();
        cua.add(origen);
        visitats.add(origen);

        while (!cua.isEmpty()) {
            Node actual = cua.poll();
            List<Canonada> canonades = grafResidual.get(actual);
            if (canonades != null) {
                for (Canonada canonada : canonades) {
                    Node v = canonada.node2();
                    if (!visitats.contains(v) && canonada.capacitatTemporal() > 0) {
                        visitats.add(v);
                        cami.put(v, canonada);
                        cua.add(v);
                        if (v.equals(terminal)) {
                            return construirCami(cami, v);
                        }
                    }
                }
            }
        }
        return null;
    }

    /** @brief Dona un posible cami (conjunt canonades) per arribar a terminal
	@pre  terminal pertany a cami 
	@post Retorna una llista de canonades que formen un camí fins a terminal
    */
    private static List<Canonada> construirCami(Map<Node, Canonada> cami, Node terminal) {

        List<Canonada> resultat = new ArrayList<>();
        Node actual = terminal;
        while (cami.containsKey(actual)) {
            Canonada canonada = cami.get(actual);
            resultat.add(canonada);
            actual = canonada.node1();
        }
        Collections.reverse(resultat);
        return resultat;
    }

    /** @brief Afegeix una canonada residual entre u i v al graf
	@pre  u i v pertanyen a graf 
	@post Afegeix una canonada residual entre u i v al graf
    */
    private static void afegirCanonadaResidual(Map<Node, List<Canonada>> graf, Node u, Node v, float capacitat) {

        List<Canonada> llistaCanonadesU = graf.computeIfAbsent(u, k -> new ArrayList<>());
        boolean trobat = false;
        for (Canonada canonada : llistaCanonadesU) {
            if (canonada.node2().equals(v)) {
                canonada.incrementarCapacitat(capacitat);
                trobat = true;
                break;
            }
        }
        if (!trobat) {
            llistaCanonadesU.add(new Canonada(u, v, capacitat));
        }

        List<Canonada> llistaCanonadesV = graf.computeIfAbsent(v, k -> new ArrayList<>());
        trobat = false;
        for (Canonada canonada : llistaCanonadesV) {
            if (canonada.node2().equals(u)) {
                trobat = true;
                break;
            }
        }
        if (!trobat) {
            llistaCanonadesV.add(new Canonada(v, u, 0));
        }
    }

    /** @brief Redueix la capacitat de la canonada entre u i v
	@pre  u i v pertanyen a graf 
	@post Redueix la capacitat (capacitat =capacitat-reduccio) de la canonada entre u i v al graf
    */
    private static void canonadaReduirCapacitat(Map<Node, List<Canonada>> graf, Node u, Node v, float reduccio) {

        for (Canonada canonada : graf.getOrDefault(u, new ArrayList<>())) {
            if (canonada.node2().equals(v)) {
                canonada.reduirCapacitat(reduccio);
                break;
            }
        }

        for (Canonada canonada : graf.getOrDefault(v, new ArrayList<>())) {
            if (canonada.node2().equals(u)) {
                canonada.incrementarCapacitat(reduccio);
                break;
            }
        }
    }

    /** @brief Construeix una xarxa residual i la dibuixa
	@pre  xOriginal i grafResidual no són nulls 
	@post Construeix la xarxa residual i la dibuixa
    */
    public static void construirXarxaResidual(Map<Node, List<Canonada>> grafResidual) {

        Xarxa xarxaResidual = new Xarxa();
        Origen superOrigen = new Origen("superOrigen", new Coordenades("0:0:0N,0:0:0E"));
        Terminal superTerminal = new Terminal("superTerminal", new Coordenades("0:0:10N,0:0:10E"), Float.MAX_VALUE);
        xarxaResidual.afegir(superOrigen);
        xarxaResidual.afegir(superTerminal);

        for (Map.Entry<Node, List<Canonada>> entry : grafResidual.entrySet()) {
            Node node = entry.getKey();
            if (!node.id().equals(superOrigen.id()) && !node.id().equals(superTerminal.id())) {
                xarxaResidual.afegir((Connexio) node);
            }
        }

        for (Map.Entry<Node, List<Canonada>> entry : grafResidual.entrySet()) {
            Node origen = entry.getKey();
            List<Canonada> canonades = entry.getValue();
            for (Canonada canonada : canonades) {
                Node desti = canonada.node2();
                if (canonada.capacitat() != 0) {
                    if (origen.id().equals(superOrigen.id())) {
                        xarxaResidual.connectarAmbCanonada(superOrigen, desti, canonada.capacitat());
                    } else if (desti.id().equals(superTerminal.id())) {
                        xarxaResidual.connectarAmbCanonada(origen, superTerminal, canonada.capacitat());
                    } else {
                        xarxaResidual.connectarAmbCanonada(origen, desti, canonada.capacitat());
                    }
                }
            }
        }

        xarxaResidual.dibuixar(superOrigen);
    }
}
