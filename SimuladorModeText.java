/** @file SimuladorModeText.java
    @brief Classe SimuladorModeText
*/

/** @class SimuladorModeText
    @brief Processa el fitxer de text fent totes les operacions necesaries
    @author Marcel·lí Corominas
*/


import java.util.*;
import java.io.*;

public class SimuladorModeText {

    private final Xarxa xarxa = new Xarxa(); /// < Es crea una nova Xarxa d'aigua
    private final GestorXarxes gestor = new GestorXarxes(); /// < Es crea una nou Gestor de Xarxes d'aigua

    ArrayList<String> operacions = new ArrayList<>(Arrays.asList("terminal", "origen", "connexio", "connectar", "abonar", "tancar", "obrir", "backtrack", "cabal", "demanda", "cicles", "arbre", "cabal minim", "exces cabal", "situacio", "cabal abonat", "proximitat", "dibuix", "max-flow")); ///< Llista del nom de totes les posibles operacions
    private final ArrayList<String> historial = new ArrayList<>(); /// < Conte els identificadors dels nodes que han passat d'aixeta oberta a aixeta tancada i viceversa


     /** @brief Recula n operacions d'obriment/tancament d'aixetes
	@pre  n>=0
	@post Recula n operacions d'obriment/tancament d'aixetes
    */

     private void recular(int n) {
         int inici = historial.size() -1;
         int fin = inici - n ;

         if (fin < 0) {
             fin = 0;
         }
         for (int i = inici; i >= fin; i--) {
             String[] operacio = historial.get(i).split(" ");
             String op = operacio[0];
             String id = operacio[1];
             Node n1 = xarxa.node(id);
             if (op.equals("obrir")) {
                 xarxa.tancarAixeta(n1);
             } else if (op.equals("tancar")) {
                 xarxa.obrirAixeta(n1);
             }

         }
         for (int i = inici; i >= fin; i--) {
             if (!historial.isEmpty()) {
                 historial.remove(i);
             }
         }
     }




    /** @brief Ens diu si op fa referencia a una operacio
	@pre  El fitxer existeix 
	@post Retorna true si op fa referencia a una operacio
    */
    private boolean esOperacio(String op){

        return operacions.contains(op);
    }

    /** @brief Processa totes les operacions del fitxer d'entrada
	@pre  El fitxer existeix 
	@post Processa totes les operacions del fitxer 
    */
    public void simular(String fitxer){
        File Sortida = new File("sortida.txt");
        try {
            if (Sortida.createNewFile()) {
                System.out.println("Fitxer creat: " + Sortida.getName());
            } else {
                System.out.println("El fitxer ja existeix.");
            }
        } catch (IOException e) {
            System.out.println("Error al crear fitxer.");
            e.printStackTrace();
            return;
        }

        try(BufferedReader reader =new BufferedReader(new FileReader(fitxer));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Sortida, true)))){
                String linia, nom, coor, dni;
                float demanda, capacitat, cabal;
                String regex = "^[^\\-]+-[^\\-]+$";
                while ((linia = reader.readLine()) != null) {
                    if (linia.equals("terminal")) {
                        nom = reader.readLine();
                        coor = reader.readLine();
                        try {
                            demanda = Float.parseFloat(reader.readLine());
                            Coordenades cord = new Coordenades(coor);
                            Terminal term = new Terminal(nom, cord,demanda);
                            xarxa.afegir(term);
                        }
                        catch(Exception e){
                            System.out.println("S'esperava un real.");
                        }

                    } else if (linia.equals("origen")) {
                        nom = reader.readLine();
                        coor = reader.readLine();
                        Coordenades cord = new Coordenades(coor);
                        Origen ori = new Origen(nom, cord);
                        xarxa.afegir(ori);
                    } else if (linia.equals("connexio")) {
                        nom = reader.readLine();
                        coor = reader.readLine();
                        Coordenades cord = new Coordenades(coor);
                        Connexio con = new Connexio(nom, cord);
                        xarxa.afegir(con);
                    } else if (linia.equals("connectar")) {
                        Node n1 = xarxa.node(reader.readLine());
                        Node n2 = xarxa.node(reader.readLine());
                        capacitat = Float.parseFloat(reader.readLine());
                        xarxa.connectarAmbCanonada(n1, n2, capacitat);
                    } else if (linia.equals("abonar")) {
                        dni = reader.readLine();
                        Node n1 = xarxa.node(reader.readLine());
                        Terminal n=(Terminal) n1;
                        xarxa.abonar(dni, n);
                    } else if (linia.equals("tancar")) {
                        nom=reader.readLine();
                        Node n1 = xarxa.node(nom);
                        if(n1.aixetaOberta()){
                            historial.add(linia + " " + nom);
                        }
                        xarxa.tancarAixeta(n1);
                    }
                    else if (linia.equals("obrir")) {
                        nom=reader.readLine();
                        Node n1 = xarxa.node(nom);
                        if(!n1.aixetaOberta()){
                            historial.add(linia + " " + nom);
                        }
                        xarxa.obrirAixeta(n1);
                    }
                    else if (linia.equals("backtrack")) {
                        int n = Integer.parseInt(reader.readLine());
                        recular(n);
                    } else if (linia.equals("cabal")) {
                        Node n1 = xarxa.node(reader.readLine());
                        cabal = Float.parseFloat(reader.readLine());
                        Origen o=(Origen) n1;
                        xarxa.establirCabal(o, cabal);
                    } else if (linia.equals("demanda")) {
                        Node n1 = xarxa.node(reader.readLine());
                        demanda = Float.parseFloat(reader.readLine());
                        Terminal n=(Terminal) n1;
                        xarxa.establirDemanda(n, demanda);
                    } else if (linia.equals("cicles")) {
                        nom=reader.readLine();
                        Node n1 = xarxa.node(nom);
                        Origen o=(Origen) n1;
                        if (gestor.teCicles(xarxa, o)) {
                            writer.println(nom+" te cicles");
                        } else {
                            writer.println(nom + " no te cicles");
                        }
                    } else if (linia.equals("arbre")) {
                        nom= reader.readLine();
                        Node n1 = xarxa.node(nom);
                        Origen o=(Origen) n1;
                        if (gestor.esArbre(xarxa, o)) {
                            writer.println(n1.id() + " es un arbre");
                        } else {
                            writer.println(n1.id() + " no es un arbre");
                        }
                    } else if (linia.equals("cabal minim")) {
                        Node n1 = xarxa.node(reader.readLine());
                        Origen o=(Origen) n1;
                        linia = reader.readLine().replaceAll("[^0-9]", "");
                        cabal = Float.parseFloat(linia);
                        writer.println("cabal minim");
                        writer.println(gestor.cabalMinim(xarxa, o, cabal));
                    }
                    else if (linia.equals("exces cabal")) {
                        Set<Canonada> canonades = new HashSet<Canonada>();
                        while (true) {
                            reader.mark(1000);
                            linia = reader.readLine();
                            if (linia == null || esOperacio(linia)) {
                                reader.reset();
                                break;
                            }

                            if(linia.matches(regex)){
                                    Canonada can = xarxa.canonada(linia);
                                    if(can!=null) {
                                        canonades.add(can);
                                    }

                            }
                            else{
                                System.out.println("\nError a les dades --> " + linia + " no és una canonada");
                            }

                        }
                        writer.println("exces cabal");
                        Set<Canonada> resultats = gestor.excesCabal(xarxa,canonades);
                        for (Canonada canonada : resultats) {
                            writer.println(canonada.node1().id()+"-"+canonada.node2().id());
                        }
                    }

                    else if (linia.equals("situacio")) {
                        Map<Terminal, Boolean> llista = new HashMap<>();

                        while (true) {
                            reader.mark(1000);
                            linia = reader.readLine();
                            if (linia == null || esOperacio(linia)) {
                                reader.reset();
                                break;
                            }
                            String[] parts = linia.split(" ");
                            if (parts.length == 2) {
                                Terminal terminal = (Terminal) xarxa.node(parts[0]);
                                Boolean repAigua = parts[1].equals("SI");
                                llista.put(terminal, repAigua);
                            }
                            else{
                                System.out.println("\nError a les dades --> " + linia);
                            }
                        }
                        Set<Node> resultats = gestor.aixetesTancar(xarxa, llista);
                        writer.println("tancar");
                        for (Node n : resultats) {
                            writer.println(n.id());
                        }
                    }

                    else if(linia.equals("cabal abonat")){
                    dni=reader.readLine();
                    cabal=xarxa.cabalAbonat(dni);
                    writer.println("cabal abonat");
                    writer.println(cabal);
                }
                else if(linia.equals("proximitat")){
                    Set<Node> aixetes = new HashSet<>();
                    coor=reader.readLine();
                    Coordenades cord = new Coordenades(coor);
                    while(true){
                        reader.mark(1000);
                        linia=reader.readLine();
                        if(linia == null || esOperacio(linia)){
                            reader.reset();
                            break;
                        }
                        Node aix = xarxa.node(linia);
                        aixetes.add(aix);
                    }
                    List<Node> resultats = GestorXarxes.proximitat(cord,aixetes);
                    writer.println("proximitat");
                    for (Node n : resultats) {
                        writer.println(n.id());
                    }
                }
                else if(linia.equals("dibuix")){
                        Node n=xarxa.node(reader.readLine());
                        xarxa.dibuixar(n);
                }
                else if(linia.equals("max-flow")){
                        Node n=xarxa.node(reader.readLine());
                        Origen o=(Origen) n;
                        GestorXarxes.fluxMaxim(xarxa, o);
                    }
                else{
                        System.out.println("Operació desconeguda: " + linia);
                }
                }




        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
