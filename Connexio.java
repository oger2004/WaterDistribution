/** @file Connexio.java
    @brief Classe Connexio
*/

/** @class Connexio
    @brief  Node de tipus connexio
    @author Roger Costa
*/

public class Connexio extends Node {
    //Descripció general: Node de connexió d'una xarxa de distribució d'aigua

    /** @brief Crea node de tipus connexio
	@pre  ---
	@post S'ha creat un nou node de connexió amb identificador id i coordenades c
    */
    public Connexio(String id, Coordenades c){
        super(id,c);
    }
   
}
