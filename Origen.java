/** @file Origen.java
    @brief Classe Origen
*/

/** @class Origen
    @brief Node de tipus origen
    @author Roger Costa
*/

public class Origen extends Node {
    //Descripció general: Node origen d'una xarxa de distribució d'aigua
    private float cabal; /// < El cabal que hi ha a origen
    
/** @brief Crea un node de tipus origen
	@pre  ---
	@post  S'ha creat un nou origen amb identificador id i coordenades c
    */
    public Origen(String id, Coordenades c){
       super(id,c);
        this.cabal=0;
    }

    /** @brief Retorna el cabal d'aigua que surt de l'origen
	@pre  ---
	@post  Retorna el cabal d'aigua que surt de l'origen
    */
    public float cabal(){
        return cabal;
    }

   /** @brief Estableix el cabal
	@pre  cabal >= 0
	@post El cabal d'aigua que surt de l'origen és cabal
    */
    //Excepcions: IllegalArgumentException si cabal < 0
    public void establirCabal(float cabal){
        try {
            if(cabal<0) throw new IllegalArgumentException("Aquest cabal no es pot establir: cabal < 0");
            this.cabal=cabal;
        } catch (IllegalArgumentException e) {
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }

}
