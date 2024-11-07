/** @file Coordenades.java
    @brief Classe Coordenades
*/

/** @class Coordenades
    @brief Gestio de les coordenades d'un node
    @author Roger Costa
*/

public class Coordenades {
    //Descripció general: Coordenades geogràfiques (latitud, longitud)
    float latitud; ///< Representacio de la latitud
    float longitud; ///< Representacio de la longitud

    /** @brief Passa de coordenades en hores,minuts, segons al equivalent amb longitud i latitud
	@pre  0 <= grausLatitud <= 60, 0 <= minutsLatitud <= 60, 
          0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S', 
          0 <= grausLongitud <= 60, 0 <= minutsLongitud <= 60,
          0 <= segonsLongitud <= 60, direccioLatitud = 'E' o 'W'
	@post Passa de coordenades en hores,minuts, segons al equivalent amb longitud i latitud
    */
    //Excepcions: IllegalArgumentException si es viola la precondició
    public Coordenades(String coor){
        try {
            String[] parts = coor.split(",");
            String[] latitudParts = parts[0].split(":");
            int grausLatitud = Integer.parseInt(latitudParts[0]);
            int minutsLatitud = Integer.parseInt(latitudParts[1]);
            float segonsLatitud = Float.parseFloat(latitudParts[2].substring(0, latitudParts[2].length() - 1)); // Eliminar la lletra N
            char direccioLatitud = latitudParts[2].charAt(latitudParts[2].length() - 1);



            String[] longitudParts = parts[1].split(":");
            int grausLongitud = Integer.parseInt(longitudParts[0]);
            int minutsLongitud = Integer.parseInt(longitudParts[1]);
            float segonsLongitud = Float.parseFloat(longitudParts[2].substring(0, longitudParts[2].length() - 1)); // Eliminar la lletra E
            char direccioLongitud = longitudParts[2].charAt(longitudParts[2].length() - 1);
            if(0 >= grausLatitud && grausLatitud>= 60) throw new IllegalArgumentException("Valor de grausLatitud ha d'estar entre 0 <= grausLatitud <= 60");
            else if(0 >= minutsLatitud && minutsLatitud>= 60)  throw new IllegalArgumentException("Valor de minutsLatitud ha d'estar entre 0 <= minutsLatitud <= 60");
            else if(0 >= segonsLatitud && segonsLatitud >= 60)  throw new IllegalArgumentException("Valor de segonsLatitud ha d'estar entre ");
            else if(direccioLatitud!='N' && direccioLatitud!='S' && direccioLatitud!='E' &&  direccioLatitud!='W') throw new IllegalArgumentException("Valor de direccioLatitud ha de ser N,S,E o W");
            else if(0 >= grausLongitud && grausLongitud >= 60)  throw new IllegalArgumentException("Valor de grausLongitud ha d'estar entre 0 <= grausLongitud <= 60");
            else if(0 >= minutsLongitud && minutsLongitud>= 60)  throw new IllegalArgumentException("Valor de minutsLongitud ha d'estar entre 0 <= minutsLongitud <= 60");
            else if(0 >= segonsLongitud && segonsLongitud>= 60)  throw new IllegalArgumentException("Valor de segonsLongitud ha d'estar entre 0 <= segonsLongitud <= 60");
            else{
                this.latitud=grausLatitud + (minutsLatitud / 60) + (segonsLatitud / 3600);
                this.longitud=grausLongitud + (minutsLongitud / 60) + (segonsLongitud / 3600);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError a les dades --> " + e.getMessage());
        }
    }

    /** @brief Retorna la distància entre this i c, expressada en km
	@pre  ---
	@post Retorna la distància entre this i c, expressada en km
    */
    public double distancia(Coordenades c){

        //De decimal a radians
        double thislatitudRad = Math.toRadians(this.latitud);
        double clatitudRad = Math.toRadians(c.latitud);

        double incremLatitud = Math.toRadians(c.latitud - this.latitud);
        double incremLongitud = Math.toRadians(c.longitud - this.longitud);

        // Formula d'haversine
        double a = Math.sin(incremLatitud / 2) * Math.sin(incremLatitud / 2) +
                   Math.cos(thislatitudRad) * Math.cos(clatitudRad) *
                   Math.sin(incremLongitud / 2) * Math.sin(incremLongitud / 2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.1 * b;
    }
}
