/** @file Xarxa.java
    @brief Classe Xarxa
*/

/** @class Xarxa
    @brief Graf dirigit, un simulador d'una xarxa d'aigua.
    @author Roger Costa
*/

import java.util.*;

import org.graphstream.graph.implementations.*;
import org.graphstream.graph.*;


public class Xarxa {
    //Descripció general: Xarxa de distribució d'aigua, no necessàriament connexa (graf dirigit de Node)
   
    private Map<Node,Set<Canonada>> entrada; ///< La llista de canonades son les que entren al Node
    private Map<Node,Set<Canonada>> sortida; ///< La llista de canonades son les que surten del Node
    private Map<String,Node> clients;        ///< Per cada client en quin node esta abonat


    /**
        @brief Crea una Xarxa buida
	    @pre  ---
	    @post Crea una xarxa de distribució d'aigua buida
    */
    public Xarxa(){
        entrada=new HashMap<Node,Set<Canonada>>();
        sortida=new HashMap<Node,Set<Canonada>>();
        clients= new HashMap<>();
    }

     /**
        @brief Busca el node id a la xarxa
	    @pre  ---
	    @post Retorna el node de la xarxa amb identificador id
    */
    public Node node(String id){

        Set<Node> e = entrada.keySet();
        for (Node elem : e) {
            String n = elem.id();
            if(n.equals(id)) return elem;
        }

        Set<Node> s = sortida.keySet();
        for (Node elem : s) {
            String n = elem.id();
            if(n.equals(id)) return elem;
        }
        return null;
    }
   
    /** @brief Torna les canonades que surten d'un node
	@pre  node pertany a la xarxa 
	@post Retorna un iterador que permet recórrer totes les canonades que surten del node
    */
    public Iterator<Canonada> sortides(Node node){
        
        Set<Canonada> val= sortida.get(node);
        if(val!=null) return val.iterator();
        else return null;
    }

    /** @brief Torna les canonades que surten d'un node
	@pre  node pertany a la xarxa
	@post Retorna un iterador que permet recórrer totes les canonades que entren al node
    */
    public Iterator<Canonada> entrades(Node node){

        Set<Canonada> val= entrada.get(node);
        if(val!=null) return val.iterator();
        else return null;
    }
     
    /** @brief Afegeix un origen a la Xarxa
	@pre  No existeix cap node amb el mateix id que nodeOrigen a la xarxa 
	@post S'ha afegit nodeOrigen a la xarxa
    */
    //Excepcions: IllegalArgumentException si ja existeix un node amb aquest id
    public void afegir(Origen nodeOrigen){
        try {
            if(sortida.containsKey(nodeOrigen)) throw new IllegalArgumentException("Ja existeix un node a la xarxa amb aquest id");

            Set<Canonada> llS=new HashSet<>();
            sortida.put(nodeOrigen,llS);

        } catch (IllegalArgumentException e) {
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
     
      /** @brief Crea una Xarxa buida
	@pre  No existeix cap node amb el mateix id que nodeTerminal a la xarxa 
	@post S'ha afegit nodeTerminal a la xarxa
    */
    
    //Excepcions: IllegalArgumentException si ja existeix un node amb aquest id
    public void afegir(Terminal nodeTerminal){
        try {
            if(entrada.containsKey(nodeTerminal)) throw new IllegalArgumentException("Ja existeix un node a la xarxa amb aquest id");

            Set<Canonada> llE=new HashSet<>();
            entrada.put(nodeTerminal,llE);

        } catch (IllegalArgumentException e) {
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
   
/** @brief Afegeix una connexio a la Xarxa
	@pre  No existeix cap node amb el mateix id que nodeConnexio a la xarxa 
	@post S'ha afegit nodeConnexio a la xarxa
    */
 
    //Excepcions: IllegalArgumentException si ja existeix un node amb aquest id
    public void afegir(Connexio nodeConnexio){
        try {
            if(entrada.containsKey(nodeConnexio)) throw new IllegalArgumentException("Ja existeix un node a la xarxa amb aquest id");
            if(sortida.containsKey(nodeConnexio)) throw new IllegalArgumentException("Ja existeix un node a la xarxa amb aquest id");

            Set<Canonada> llE=new HashSet<>();
            entrada.put(nodeConnexio,llE);

            Set<Canonada> llS=new HashSet<>();
            sortida.put(nodeConnexio,llS);

        } catch (IllegalArgumentException e) {
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }

    /** @brief Connecta dos nodes amb una canonada de capacitat c
	@pre  node1 i node2 pertanyen a la xarxa, no estan connectats, i node1 no és un node terminal 
	@post S'han connectat els nodes amb una canonada de capacitat c, amb sentit de l'aigua de node1 a node2
    */
   
    //Excepcions: NoSuchElementException node1 o node2 no pertanyen a la xarxa
    //            IllegalArgumentException els nodes ja estan connectats o node1 és un node terminal
    public void connectarAmbCanonada(Node node1, Node node2, float c){
        try{
            if(node1 instanceof Terminal) throw new IllegalArgumentException("No es pot connectar, node1 es Terminal");

            try{
                Set<Canonada> l2 = entrada.get(node2);
                Set<Canonada> l1 = sortida.get(node1);

                if(l1 == null) throw new NoSuchElementException("No existeix el node1 a la xarxa");
                if(l2 == null) throw new NoSuchElementException("No existeix el node2 a la xarxa");

                Canonada can=new Canonada(node1,node2,c);
                if(l1.contains(can)) throw new IllegalArgumentException("Els nodes ja estan connectats");

                entrada.get(node2).add(can);
                sortida.get(node1).add(can);

            }
            catch (NoSuchElementException f){
                System.out.println("\nError a les dades --> " + f.getMessage());
            }
        }
        catch (IllegalArgumentException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
     
 /** @brief Abona un clinet a una terminal
	@pre  nodeTerminal pertany a la xarxa 
	@post El client identificat amb idClient queda abonat al node terminal
    */
   
    //Excepcions: NoSuchElementException si nodeTerminal no pertany a la xarxa
    public void abonar(String idClient, Terminal nodeTerminal){
        try{
            
            if (entrada.get(nodeTerminal)==null) throw new NoSuchElementException("El node terminal no existeix a la Xarxa1");

            Node sort= buscar(nodeTerminal, entrada);
            clients.put(idClient,sort);

            if (sort instanceof Terminal){
                Terminal t = (Terminal) sort;
                t.afegirAbonat(idClient);
            }   
        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
    
 /** @brief Obre l'aixeta del node
	@pre  node pertany a la xarxa 
	@post  L'aixeta del node està oberta
    */
    
    //Excepcions: NoSuchElementException si node no pertany a la xarxa
    public void obrirAixeta(Node node){
        try{
            if(entrada.get(node)==null && sortida.get(node)==null) throw new NoSuchElementException("El node no pertany a la xarxa2");
            Node entr= buscar(node, entrada);
            Node sort= buscar(node, sortida);
           
            if(sort!=null)sort.obrirAixeta();
            if(entr!=null) entr.obrirAixeta();
        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
    
/** @brief Busca el node b en el map entrat
	@pre  S'entra un node b que pertany a la xarxa un map inicialitzat 
	@post Retorna el node que hi ha el map que sigui igual que b
    */
    private Node buscar(Node b, Map<Node,Set<Canonada>> map){
        Set<Node> s=map.keySet();
        Iterator<Node> it=s.iterator();

        while(it.hasNext()){
            Node n=it.next();
            if(n.equals(b)) {
                return n;
            }
        }
        return null;
    }
     
    
/** @brief Tanca l'aixeta del node
	@pre  node pertany a la xarxa 
	@post Tanca l'aixeta del node
    */
    //Excepcions: NoSuchElementException si node no pertany a la xarxa
    public void tancarAixeta(Node node){
        try{
            if(entrada.get(node)==null && sortida.get(node)==null) throw new NoSuchElementException("El node no pertany a la xarxa3");

            Node entr= buscar(node, entrada);
            Node sort= buscar(node, sortida);

            if(sort!=null) sort.tancarAixeta();
            if(entr!=null) entr.tancarAixeta();

        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
     
    
/** @brief Estableix el cabal que hi ha en un origen
	@pre  nodeOrigen pertany a la xarxa i cabal >= 0 
	@post El cabal de nodeOrigen és cabal
    */
   
    //Excepcions: NoSuchElementException si nodeOrigen no pertany a la xarxa
    //            IllegalArgumentException si cabal és negatiu
    public void establirCabal(Origen nodeOrigen, float cabal){
        try{
            if(sortida.get(nodeOrigen)==null) throw new NoSuchElementException("El node no pertany a la xarxa4");

            try{
                if(cabal<0) throw new IllegalArgumentException("Cabal no pot ser negatiu cabal ha de ser >=0");

                Node entr= buscar(nodeOrigen, sortida);
                if (entr instanceof Origen){
                    Origen o = (Origen) entr;
                    o.establirCabal(cabal);
                }

            }catch (IllegalArgumentException e){
                System.out.println("\nError a les dades --> " + e.getMessage());
            }
        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
     

/** @brief Estableix la demanda en un node terminal
	@pre  nodeTerminal pertany a la xarxa i demanda >= 0 
	@post La demanda de nodeTerminal és demanda
    */
    //Excepcions: NoSuchElementException si nodeTerminal no pertany a la xarxa
    //            IllegalArgumentException si demanda és negatiu
    public void establirDemanda(Terminal nodeTerminal, float demanda){
        try{
            Node n= buscar(nodeTerminal, entrada);
            if(n==null) throw new NoSuchElementException("El node terminal no pertany a la xarxa5");

            try{
               if(demanda < 0) throw new IllegalArgumentException("La demanda no pot ser negativa");

               Terminal t= (Terminal) n;

               t.establirDemandaActual(demanda);
            }
            catch (IllegalArgumentException e){
               System.out.println("\nError a les dades --> " + e.getMessage());
            }
        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
     
    /** @brief Distribueix per als nodes de sobre la demanda que es propaga
	@pre  ll inicialitzada, suma>=0, terminal determina si el node ant és terminal 
	@post Mira quantes canonades entren al node i distribueix el valor de suma proporcionalment
    */
    private float distribuirPerSobre(Set<Canonada> can,float suma, boolean terminal, Node ant){
        Iterator<Canonada> it = can.iterator();
        float sumCap = 0;
        Terminal t = null;
        float capacitatCan = 0;
        while(it.hasNext()){
            Canonada c = it.next();
            sumCap = sumCap + c.capacitat();
            
            if(c.node1().equals(ant)){
                if(terminal) t = (Terminal) c.node2();
                capacitatCan = c.capacitat();
            }
        }

        float prov=0;
        if(terminal) prov= suma + (capacitatCan/sumCap)* t.demandaActual();
        else prov = ((capacitatCan/sumCap)*suma);

        if(terminal && capacitatCan >= prov)
            return prov; // Si terminal capacitat canonada hi cap la demanda
        else if (terminal)
            return capacitatCan; // Si terminal capacitat canonada NO hi cap la demanda
        else if (capacitatCan>= prov)
            return prov; // Si NO terminal capacitat canonada hi cap la demanda
        else
            return capacitatCan; // Si NO terminal capacitat canonada NO hi cap la demanda
    }
     

/** @brief Retorna suma de demandes per sota el node
	@pre  node pertany a la xarxa 
	@post Retorna la demanda teòrica al Node parar segons la configuració actual de la xarxa
    */
    public float demandaI(Node node, Node ant,Node inicial){
        float suma=0;
        Set<Canonada> canonades = sortida.get(node);
      //  if(!node.aixetaOberta()) return 0;
        if (canonades != null) {
            Iterator<Canonada> itC = canonades.iterator();
            while(itC.hasNext()){
                Node n2 = itC.next().node2();
                suma = suma + demandaI(n2,node,inicial);
            }
            //Un cop trobada les demandes de per sota mirar per sobre si es reparteix per dos o mes i retornar la demanda al que volem
            Set<Canonada> can= entrada.get(node);
            if(can!=null && can.size()>1 && node!=inicial){
                if(node.aixetaOberta()) return distribuirPerSobre(can,suma, false,ant);
                else return 0;
            }
            if(node.aixetaOberta()) return suma;
            else return 0;
        }
        else if (node instanceof Terminal){
            Set<Canonada> can = entrada.get(node);
            Iterator<Canonada> it = can.iterator();


            if (can != null && can.size() == 1) {
                Canonada c = it.next();
                Terminal t = (Terminal) c.node2();
                float prov = suma + t.demandaActual();

                if (c.capacitat() < t.demandaActual())
                    suma = suma + c.capacitat();
                else suma = prov;

                if(node.aixetaOberta()) return suma;
                else return 0;
            } else {
                if(node.aixetaOberta()) return distribuirPerSobre(can, suma, true, ant);
                else return 0;
            }

        }
        return 0;
    }
    
     /** @brief  Retorna la demanda teòrica al node segons la configuració actual de la xarxa
	@pre  node pertany a la xarxa 
	@post Retorna la demanda teòrica al node segons la configuració actual de la xarxa
    */
  
    //Excepcions: NoSuchElementException si node no pertany a la xarxa
    public float demanda(Node node){

    // Recorrer tots els que estan per sota els visitats
    // Quan sarriba a terminal mirar quantes canonades te ajuntades i repartir proporcionalment en els node superiors
    // Si aixeta d'un esta tancada en aquell punt demanda es 0 
    // Si capacitat canonada < cabalNode2(), que connecta el cabal en el cabalNode1()= capacitat

        try{
            if(entrada.get(node)==null && sortida.get(node)==null) throw new NoSuchElementException("El node no pertany a la xarxa6");

            if(node instanceof Terminal){
                Terminal t= (Terminal) node;
                if(node.aixetaOberta()) return t.demandaActual();
                else return 0;
            }
            else{
                Node n=buscar(node, sortida);
                
                return demandaI(n, null,node);
            }
        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
        return 0;
    }
    
    /** @brief Crea una Xarxa buida
	@pre  id conte els id de dos node separats per guip p.e O1-C1, els dos nodes han de pertanyer a la xarxa 
	@post  Es retorna la canonada que conte que esta formada per els dos nodes del id
    */
    public Canonada canonada(String id){
        String[] opr = id.split("-");
        Node n1=node(opr[0]);
        String n2=opr[1];
        Set<Canonada> ll= sortida.get(n1);

        Iterator<Canonada> it=ll.iterator();

        while(it.hasNext()){
            Canonada c=it.next();
            if(c.node2().id().equals(n2)) {
                return c;
            }
        }

        return null;
    }
     

/** @brief Fa una llista de la suma de demandes que te per sota el node
	@pre  node pertany a la xarxa 
	@post Retorna una llista de float amb el valor de les demandes dels nodes als que distribueix aigua node
    */
    private List<Float> demandesNode(Node node){
        Set<Canonada> ll = sortida.get(node);
        float sum = 0;
        List<Float> demandes = new ArrayList<>();

        if(ll!=null){

            Iterator<Canonada> itC = ll.iterator();

            while(itC.hasNext()){
                Node n2 = itC.next().node2();
                demandes.add(demanda(n2));
            }
        }

        return demandes;
    }
     
   
/** @brief Fa una suma d'una llista de floats
	@pre  ll ha d'estar inicialitzat, pot estar buida 
	@post Retorna la suma dels valors de ll
    */
    private float sumaDemandesNode(List<Float> ll) {

        Iterator<Float> it = ll.iterator();
        float res = 0;
        while (it.hasNext()) {
            res= res + it.next();
        }
        return res;
    }

/** @brief Retorna el cabal teòric al node segons la configuració actual de la xarxa
	@pre  El node ha de ser diferent de null i ha de trobar-se a la xarxa, ant es la canonada anterior en la primera iteració null 
	@post Retorna el cabal teòric al node segons la configuració actual de la xarxa
    */
    private float cabalI(Node node,Canonada ant){
        float suma=0;
        Set<Canonada> canonades = entrada.get(node);
        if(demanda(node)==0) return (float) 0.0;
        if(canonades != null) {
            Iterator<Canonada> itCan = canonades.iterator();

            while (itCan.hasNext()) {

                Canonada c = itCan.next();
                Node n1 = c.node1();
                suma = suma + cabalI(n1, c);
            }
            float sDem = sumaDemandesNode(demandesNode(node));

            if(ant!=null){
                float prov = (demanda(ant.node2()) / sDem) * suma;

                if(ant.capacitat() < prov && node.aixetaOberta()) return ant.capacitat();
                else if (sDem > suma){

                    if(ant.capacitat()<prov && node.aixetaOberta()) return ant.capacitat();
                    else if (node.aixetaOberta()) return prov;
                }
                else if( sDem <= suma && node.aixetaOberta()) {
                    //Vol dir que en el node tenim cabal sobrant per tant podem dir que hi ha només el que necessita
                    if(ant.capacitat() >= demanda(ant.node2()))return demanda(ant.node2());
                    else return ant.capacitat();
                }
                else return 0;
            }
            else if(node.aixetaOberta()) {

                return suma;
            }
            else return 0;
        }
        else {
            
            List<Float> ll = demandesNode(node);
            float sumDemandesNode = sumaDemandesNode(ll);

            if (node instanceof Origen) {
                Origen o = (Origen) node;
                if(o.aixetaOberta()) {
                    if (o.cabal() >= sumDemandesNode) {

                        float prov = (demanda(ant.node2()) / sumDemandesNode) * o.cabal();

                        if( demanda(o) > o.cabal() && o.cabal() <= ant.capacitat() && ll.size()==1)
                            return o.cabal();
                        else if ( demanda(o)  > o.cabal() && o.cabal() <= ant.capacitat() && ll.size()>1 ){

                            if(ant.node2() instanceof Terminal) return distribuirPerSobre(entrada.get(ant.node2()),0,true,ant.node1());
                            else return distribuirPerSobre(entrada.get(ant.node2()),0,false,ant.node1());

                        }
                        else if ( demanda(o)  > o.cabal() && prov > ant.capacitat())
                            return ant.capacitat();

                        else if ( demanda(o) <= o.cabal() && ll.size()==1){

                            return demanda(o);
                        }

                        else if ( demanda(o) <= o.cabal() && ll.size()>1){

                            if(ant.node2() instanceof Terminal) return distribuirPerSobre(entrada.get(ant.node2()),0,true,ant.node1());
                            else return distribuirPerSobre(entrada.get(ant.node2()),demanda(ant.node2()),false,ant.node1());

                        }
                        else if ( demanda(o)  > o.cabal() && demanda(o) > ant.capacitat())
                            return ant.capacitat();

                    } else {

                        float prov = (demanda(ant.node2()) / sumDemandesNode) * o.cabal();

                        if (ant.capacitat() < prov) return ant.capacitat();
                        else return prov;
                    }
                }
                else return 0;
            }
        }
        return 0;
    }
    
   
/** @brief Retorna el cabal teòric al node segons la configuració actual de la xarxa
	@pre  node pertany a la xarxa 
	@post Retorna el cabal teòric al node segons la configuració actual de la xarxa
    */
    
    //Excepcions: NoSuchElementException si node no pertany a la xarxa
    public float cabal(Node node){

        //Recorregut cap avall, des de punts d'origen a terminals
        // Si cabal en un punt és suficient per servir demanda canonades per sota (subministrar cabal demanat)
        // Si cabal disponible és insuficient és distribuira proporcionalment

        try{

            if(entrada.get(node)==null && sortida.get(node)==null) throw new NoSuchElementException("El node no pertany a la xarxa7");

            if(node instanceof Origen){
                Origen o = (Origen) node;
                if(o.aixetaOberta()) return o.cabal();
                else return 0;
            }

            Node n=buscar(node, sortida);
            if(sortida.get(node)==null){
                n = buscar (node,entrada);
                return cabalI(node, null);
            }
            return cabalI(node, null);

        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
        return 0;

    }
    

/** @brief Posa els atributs necessaris a la hora de fer el mostra en un Terminal
	@pre  El valor de node es una instancia de Terminal 
	@post Es retorna un string que conte les dades propies de un Terminal
    */
    private String atributsTerminal(Node node){

        Terminal t = (Terminal) node;
        String dP = Float.toString(t.demanda());
        String dA = Float.toString(t.demandaActual());

        String cord = t.coordenades().toString();

        dP = node.id().concat(" --> ").concat(dP.concat("/"));
        dP = dP.concat(dA);

        return dP;
    }
    
   
/** @brief S'afegeixen atributs al node del graf
	@pre  En el graph si posaran les dades que necessaries que conte node 
	@post S'ha modificat graph amb les dades corresponents al node
    */
    private void afegirAtributs(Node node,Graph graph){
        org.graphstream.graph.Node nodeGraph = graph.getNode(node.id());
        if (node instanceof Terminal) {
            //La demanda punta i la demanda actual a cada punt terminal

            String str = atributsTerminal(node);
            nodeGraph.setAttribute("label", str);
        } else {

            String demanda = Float.toString(demanda(node));
            String cabal = Float.toString(cabal(node));
            String cord =node.id().concat(" --> ").concat(demanda).concat("/").concat(cabal);
            nodeGraph.setAttribute("label",cord);
        }
        graph.getNode(node.id());
        if (!node.aixetaOberta()) nodeGraph.addAttribute("ui.class", "close");

        float x = node.coordenades().latitud;
        float y = node.coordenades().longitud;
        nodeGraph.addAttribute("xy",x,y);
    }

    /** @brief Diu el cabal que va a la canonada c
     @pre  node i c pertanyen a la xarxa, it1 està inicialitzat
     @post Retorna el cabal de c donat per node
     */

    private float cabalCanonada(Node node, Canonada c, Iterator<Canonada> it1){
        float cabalN2;
        List<Float> ll2 = demandesNode(node);
        float sumDemandesNode = sumaDemandesNode(ll2);
        if(demanda(c.node2())==0) return (float) 0.0;
        if(node instanceof Origen) {
            Origen o = (Origen) node;

            if(o.aixetaOberta()) {
                if (o.cabal() >= sumDemandesNode) {

                    float prov = (demanda(c.node2()) / sumDemandesNode) * o.cabal(); // 166.66665

                    if( demanda(o) > o.cabal() && o.cabal() <= c.capacitat() && ll2.size()==1)
                        cabalN2 = o.cabal();
                    else if ( demanda(o)  > o.cabal() && o.cabal() <= c.capacitat() && ll2.size()>1 ){

                        if(c.node2() instanceof Terminal) cabalN2 = distribuirPerSobre(entrada.get(c.node2()),0,true,c.node1());
                        else cabalN2 = distribuirPerSobre(entrada.get(c.node2()),0,false,c.node1());

                    }
                    else if ( demanda(o)  > o.cabal() && prov > c.capacitat())
                        cabalN2 = c.capacitat();

                    else if ( demanda(o) <= o.cabal() && ll2.size()==1){

                        cabalN2 = demanda(o);
                    }

                    else if ( demanda(o) <= o.cabal() && ll2.size()>1){

                        if(c.node2() instanceof Terminal) cabalN2 = distribuirPerSobre(entrada.get(c.node2()),0,true,c.node1());
                        else cabalN2 = distribuirPerSobre(entrada.get(c.node2()),demanda(c.node2()),false,c.node1());

                    }
                    else if ( demanda(o)  > o.cabal() && demanda(o) > c.capacitat())
                        cabalN2 = c.capacitat();

                    else cabalN2 = (float) 0.0;

                } else {

                    float prov = (demanda(c.node2()) / sumDemandesNode) * o.cabal();

                    if (c.capacitat() < prov) cabalN2 = c.capacitat();
                    else cabalN2 = prov;
                }
            }
            else return 0;
        }
        else if(entrada.get(c.node2())!= null && entrada.get(c.node2()).size()>1) {
            float sumCap = 0;
            Terminal t = null;
            float capacitatCan = 0;
            while (it1.hasNext()) {
                Canonada c1 = it1.next();
                sumCap = sumCap + c.capacitat();

                if (c1.equals(c)) {
                    capacitatCan = c.capacitat();
                }
            }
            cabalN2 = cabal(c.node1()) * ((demanda(node) / sumCap));
        }
        else{
            cabalN2 = cabal(c.node2());
        }
        return cabalN2;
    }

    
    /** @brief Crea un graph amb la llibreria graphStream
	@pre  graph inicialitzat 
	@post Afegeix tots els nodes i arestes de la xarxa al graph amb el cabal, demanda coordenades,estat de les aixetes, sentit de l'aigua
    */

    private void dibuixI(Graph graph) {
        Iterator<Node> s = sortida.keySet().iterator();

        while (s.hasNext()) {
            Node node = s.next();
            Set<Canonada> ll = sortida.get(node);
            org.graphstream.graph.Node existeix = graph.getNode(node.id());

            if (existeix == null) {
                graph.addNode(node.id());
                afegirAtributs(node, graph);
            }
            Iterator<Canonada> itCan = ll.iterator();

            while (itCan.hasNext()) {
                Canonada c = itCan.next();
                Node n2 = c.node2();

                org.graphstream.graph.Node nodeGraph = graph.getNode(n2.id());
                if(nodeGraph == null) {
                    graph.addNode(n2.id());
                    afegirAtributs(n2, graph);
                }
                //La capacitat i el cabal actual a cada canonada
                String cap = Float.toString(c.capacitat());
                String cabal;

                Set<Canonada> ll1 = sortida.get(node);
                Iterator<Canonada> it1 = ll1.iterator();

                Float cabalN2 = cabalCanonada(node,c,it1);

                cabal = Float.toString(cabalN2);

                cap = cap.concat("/");
                String capCab = cap.concat(cabal);
                String identAresta = node.id().concat(n2.id());

                org.graphstream.graph.Edge existeixEdge = graph.getEdge(identAresta);
                if (existeixEdge == null) graph.addEdge(identAresta, node.id(), n2.id(), true);

                org.graphstream.graph.Edge e = graph.getEdge(identAresta);
                if (e != null) e.setAttribute("label", capCab);

            }
        }
    }
    
   
/** @brief Crea i mostra un graf amb graphStream
	@pre  --- 
	@post Dibuixa la xarxa de distribució d'aigua
    */
    public void dibuixar(Node n){

        try {
            if (entrada.get(n)==null && sortida.get(n)==null) throw new NoSuchElementException("El node no pertany a la xarxa8");
            Graph graph = new SingleGraph("id");

            dibuixI(graph);

            String css = "node { size: 10px; text-size: 15px; }" +
                         "node.close { fill-color: red; }";  // Estilo para nodos abiertos
            graph.setAttribute("ui.stylesheet", css);

            graph.display();
        }
        catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
    
    /** @brief Retorna el cabal d'un abonat
	@pre   Existeix un client identificat amb idClient a la xarxa 
	@post Retorna el cabal actual al punt d'abastament del client identificat amb idClientç
    */
    //Excepcions: NoSuchElementException si idClient no es troba a la Xarxa

    public float cabalAbonat(String idClient){
        try{
            if(!clients.containsKey(idClient)) throw new NoSuchElementException("El client no pertany a la xarxa");

            Node n = clients.get(idClient);
            return cabal(n);

        }catch (NoSuchElementException e){
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
        return 0;
    }

    /** @brief Retorna tots els origens conectats a node
	@pre  El node n pertany a la xarxa, res i visitats han d'estar inciialitzats  
	@post Retorna tots els origens conectats a node, i els guarda a res, deixa de guardar resultats si n esta dins de visitats
    */
    private void origensI(Node n,Set<Node> res,Queue<Node> visitats){

        Iterator<Canonada> itE = null;
        Set<Canonada> e = entrada.get(n);
        if(e!=null) itE = e.iterator();
        visitats.add(n);
        while(itE!=null && itE.hasNext()){
            Canonada cE= itE.next();
            Node node = cE.node1();

            if(node instanceof Origen) res.add(node);
            else if (!visitats.contains(node)) origensI(node,res,visitats);
        }

        Iterator<Canonada> itS = null;
        Set<Canonada> sort = sortida.get(n);
        if(sort!=null) itS = sort.iterator();

        while(itS!=null && itS.hasNext()){
            Canonada cS= itS.next();
            Node node = cS.node2();
            if(!(cS.node2() instanceof Origen) && !visitats.contains(node)) origensI(node,res,visitats);
        }
    }
   
     /** @brief Retorna tots els origens conectats a o
	@pre  Origen pertany a la xarxa  
	@post Retorna tots els origens conectats a o
    */
    public Set<Node> origensXarxa(Origen o){
        Set<Node> res = new HashSet<>();

        Set<Canonada> s = sortida.get(o);
        res.add(o);
        Queue<Node> visitats = new LinkedList<>();
        origensI(o,res,visitats);

        return res;
    }
}
