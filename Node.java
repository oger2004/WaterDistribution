/** @file Node.java
    @brief Classe Node
*/

/** @class Node
    @brief Un node de la xarxa d'aigua
    @author Roger Costa
*/

public class Node {
    //Descripció general: Node d'una xarxa de distribució d'aigua
    private final String id; ///< El nom del node
    private final Coordenades c; ///< Les coordenades del node
    private boolean aixetaOberta; ///< Estat de la aixeta true = aixetaOberta, false= aixetaTancada

    /** @brief es crea un node
	@pre  ---
	@post S'ha creat un nou node amb identificador id i coordenades c
    */
    public Node(String id, Coordenades c){
        this.id=id;
        this.c=c;
        aixetaOberta=true;
    }
    
    /** @brief Retorna l'identificador del node
	@pre  ---
	@post Retorna l'identificador del node
    */
    public String id(){
        return id;
    }
   
    /** @brief Retorna les coordenades del node
	@pre  ---
	@post Retorna les coordenades del node
    */
    public Coordenades coordenades(){
        return c;
    }


    /** @brief  Diu si l'aixeta del node està oberta
	@pre  ---
	@post Diu si l'aixeta del node està oberta(true)
    */
    public boolean aixetaOberta(){
        return this.aixetaOberta;
    }
    
    /** @brief Obre l'aixeta del node
	@pre  ---
	@post  L'aixeta del node està oberta
    */
    public void obrirAixeta(){
        aixetaOberta=true;
    }
    
    /** @brief Tanca l'aixeta del node
	@pre  ---
	@post L'aixeta del node està tancada
    */
    public void tancarAixeta(){
        aixetaOberta=false;
    }
    
}
