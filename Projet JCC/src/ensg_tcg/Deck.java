package ensg_tcg;
/**
 * @author Parant Maxime, Beauvallet Clement
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Deck {
	
	private List<Carte> cartes;
	private String nom;
	
	public Deck(String nom) {
		/**
		 * Constructeur du deck vide avec un nom
		 * @param String nom : nom du deck.
		 */
		this.nom =nom;
	}
	
	public String getNom() {
		/**
		 * @return String : nom du deck
		 */
		return this.nom;
	}
	
	public List<Carte> getCartes(){
		/**
		 * @return List<Carte> : Liste des cartes dans le deck.
		 */
		return this.cartes;
	}
	
	public void setCartes(List<Carte> cartes) {
		/**
		 * Methode creant les cartes du deck a partir de la liste connue des cartes.
		 * @param List<Carte> cartes : Liste des cartes a mettre dans le deck.
		 */
		this.cartes = cartes;
	}
	
	
	public void setDeckalea(Connection conn, int nb_cartes) throws SQLException {
		/**
		 * Methode creant un deck pour un ordinateur ou une pioche de 7 cartes pour la draft en piochant
		 * les cartes aleatoirement dans la database.
		 * @param Connection conn : connection a la base de donnee.
		 * @param int nb_cartes : nombre de cartes a piocher (6 ou 7 suivant l'utilisation).
		 */
		
		List<Carte> cartesalea = new ArrayList<>();
		Statement state = conn.createStatement();
		for (int l=1; l<=nb_cartes; l++) {
			
			ResultSet result = state.executeQuery("SELECT * FROM cartes ORDER BY id ASC");
			int n = 0;
			while(result.next()) {
				n+=1;
			}
			boolean ok =true;
			int id = 0;
			//pioche de la carte aleatoirement et recuperation de son id.
			while(ok ==true) {
				ok=false;
				double x = Math.random()*n;
				id = (int) x +1;
				for(Carte c:cartesalea) {
					if (c.getId()==id) { //Verification si la carte a deja ete piochee.
						ok=true;
					}
					
				
				}
				if (nb_cartes==6) { //Bloc verification de si la carte est disponible en ouvert (pour le deck de l'ordinateur).
					PreparedStatement ps = conn.prepareStatement("SELECT ouvert FROM cartes WHERE id = ?");
					ps.setInt(1, id);
					result = ps.executeQuery();
					result.next();
					if (result.getBoolean(1)== false) {
						ok=true;
					}
				}
				
			}
			
			
			Carte carte_alea = new Carte(id,conn);//Creation de la carte piochee a partir de son id.
			cartesalea.add(carte_alea);
		}
		this.cartes = cartesalea;
	}

}
