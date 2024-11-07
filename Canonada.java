/** @file Canonada.java
    @brief Classe Canonada
*/

/** @class Canonada
    @brief  Una canonada d'aigua entre dos nodes, de la xarxa d'aigua
    @author Roger Costa
*/

public class Canonada {
    //Descripció general: Canonada de la xarxa de distribució d'aigua

    private final Node node1; /// < Node inici de la canonada
    private final Node node2; /// < Node fi de la canonada
    private float capacitat; /// < Capacitat de la canonada
    private float capacitatTemporal; ///< Capacitat de la canonada variant

    /** @brief Crea una canonada que connecta node1 i node2 amb la capacitat indicada
	@pre  capacitat > 0
	@post Crea una canonada que connecta node1 i node2 amb la capacitat indicada
    */
    public Canonada(Node node1, Node node2, float capacitat){
        this.node1=node1;
        this.node2=node2;
        this.capacitat=capacitat;
        this.capacitatTemporal=capacitat;
    }
   
/** @brief Retorna el node d'inici de la canonada
	@pre ---
	@post Retorna el node d'inici de la canonada
    */
    public Node node1(){
        return this.node1;
    }
   

    /** @brief Retorna el node de fi de la canonada
	@pre ---
	@post Retorna el node de fi de la canonada
    */
    public Node node2(){
        return this.node2;
    }
   
    /** @brief Retorna la capacitat de la canonada
	@pre ---
	@post Retorna la capacitat de la canonada
    */
    public float capacitat(){
        return this.capacitat;
    }
    
/** @brief Retorna la capacitat temporal
	@pre ---
	@post Retorna la capacitat temporal de la canonada
    */
    public float capacitatTemporal(){
        return this.capacitatTemporal;
    }

    /** @brief Redueix la capacitat temporal en quantitat
	@pre quantitat>0
	@post Redueix la capacitat temporal en quantitat
    */
    public void reduirCapacitat(float quantitat) {
        this.capacitatTemporal -= quantitat;
    }

    /** @brief Incrementa la capacitat temporal en quantitat
	@pre quantitat>0
	@post Incrementa la capacitat temporal en quantitat
    */
    public void incrementarCapacitat(float quantitat) {
        this.capacitatTemporal += quantitat;
    }
}
