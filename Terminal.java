/** @file Terminal.java
    @brief Classe Terminal
*/

/** @class Terminal
    @brief Node de tipus terminal
    @author Roger Costa
*/

import java.util.Set;
import java.util.HashSet;
public class Terminal extends Node {

    //Descripció general: Node terminal d'una xarxa de distribució d'aigua
    private final float demandaPunta; ///< Representa la demanda punta en el terminal
    private float demandaActual; /// < Representa la demanda actual en el terminal
    private Set<String> clients; /// < El conjunt de clients en aquesta xarxa


/** @brief Es crea un nou terminal amb identificador id, coordenades c i demanda punta demanda en l/s
	@pre  ---
	@post S'ha creat un nou terminal amb identificador id, coordenades c i demanda punta demanda en l/s
    */
    public Terminal(String id, Coordenades c, float demandaPunta){
        super(id,c);
        this.demandaPunta=demandaPunta;
        demandaActual=0;
        clients = new HashSet<>();
    }
     

/** @brief 
	@pre ---
	@post Retorna la demanda punta d'aigua del terminal
    */
    public float demanda(){
        return this.demandaPunta;
    }
     
    /** @brief La demanda d'aigua actual del terminal passa a ser demanda
	@pre   demanda >= 0
	@post La demanda d'aigua actual del terminal és demanda
    */
   //Excepcions: IllegalArgumentException si demanda < 0
    public void establirDemandaActual(float demanda){
        try {
            if(demanda<0) throw new IllegalArgumentException("Aquesta demanda no es pot establir: demanda < 0");

            this.demandaActual=demanda;
        } catch (IllegalArgumentException e) {
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }
    
    /** @brief afegeix un abonat al terminal
	@pre   ---
	@post retirna true si existeix el client de primeres a la xarxa, si no exixteix s'fageix
    */
    public boolean afegirAbonat(String client){
        if(clients.contains(client)) return true;

        clients.add(client);
        return false;
    }

    /** @brief retorna la demanda actual
	@pre  --- 
	@post retorna la demanda actual 
    */
    public float demandaActual(){return this.demandaActual;}

}
