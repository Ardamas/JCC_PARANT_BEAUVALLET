package ensg_tcg;
/**
 * 
 * @author Parant Maxime, Beauvallet Clement
 *
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Joueur {
	private String nom;
	private int id;
	private int score;
	private List<Deck> decks;
	private Deck deck;
	private int pointschamp;
	
	
	
	public Joueur(String nom) {
		/**
		 * Constructeur creant un joueur avec un nom et un identifiant.
		 * @param String nom : nom du joueur.
		 */
		int identifiant = 1;
		this.nom = nom;
		this.id = identifiant;
		identifiant +=1;
		
	}
	
	public Deck getDeck() {
		/**
		 * @return Deck : Deck du joueur pendant la partie ou le tournoi.
		 */
		return this.deck;
	}
	
	public void setDeck(Deck deck) {
		/**
		 * Attribue au joueur un deck choisi dans la liste de ses decks qu'il jouera pedant la partie ouverte ou le tournoi.
		 * @param Deck deck : deck choisi dans la liste de decks du joueur.
		 */
		this.deck = deck;
	}
	
	public int getScore() {
		/**
		 * @return int : score du joueur au cours de la partie.
		 */
		return this.score;
	}
	
	public void setScore(int score) {
		/**
		 * Met a jour le score du joueur au cours d'une partie.
		 * @param int score : Nouveau score.
		 */
		this.score = score;
	}
	
	public int getPointschamp() {
		/**
		 * @return int : Points de victoire du joueur dans le championnat.
		 */
		return this.pointschamp;
	}
	
	public void setPointschamp(int points) {
		/**
		 * Attribue au joueur les points de victoire qu'il a gagne au cours d'un championnat lui permettant de le classer.
		 * @param int points : points de victoire du joueur.
		 */
		this.pointschamp += points;
	}
	
	public String getNom() {
		/**
		 * @return String : nom du joueur.
		 */
		return this.nom;
	}
	
	public List<Deck> getDecks(){
		/**
		 * @return List<Deck> : decks des joueurs.
		 */
		return this.decks;
	}
	
	public void setDecks(List<Deck> decks) {
		/**
		 * METHODE OBSOLETE
		 * Met a jour la liste des decks du joueur.
		 * @List<Deck> decks : nouvelle liste de decks du joueur.
		 */
		this.decks = decks;
	}
	
	public void setDecks(Connection conn, String j) throws SQLException {
		/**
		 * Cree la liste de decks du joueur en la recuperant dans la database.
		 * @param Connection conn : Connection a la database.
		 * @param String j : Nom du joueur concerne.
		 */
		
		//Recuperation des noms des decks du joueur dans la database.
		PreparedStatement t = conn.prepareStatement("SELECT deck1,deck2,deck3,deck4 FROM joueurs Where nom = ?");
		t.setString(1, j);
		ResultSet decksjoueur = t.executeQuery();
		decksjoueur.next();
		List<Deck> decksj = new ArrayList<>();
		
		for(int i = 1 ; i<=4; i++) {
			decksjoueur.getString(i);
			if(! decksjoueur.wasNull()) {
				//Recuperation des noms des cartes dans chaque deck.
				PreparedStatement s = conn.prepareStatement("SELECT carte1,carte2,carte3,carte4,carte5,carte6,nom FROM deck WHERE nom = ?");
				s.setString(1, decksjoueur.getString(i));
				ResultSet cartesdeck = s.executeQuery();
				cartesdeck.next();
				List<Carte> cartes = new ArrayList<>();
				for(int c=1; c<=6; c++) {
					//Recuperation des donnees de chaque carte.
					PreparedStatement st = conn.prepareStatement("Select id FROM cartes WHERE nom=?");
					
					st.setString(1, cartesdeck.getString(c));
					ResultSet attcarte = st.executeQuery();
					attcarte.next();
					//Creation de l'objet Carte
					Carte carte = new Carte(attcarte.getInt(1), conn);
					//Creation de la liste des cartes
					cartes.add(carte); 
					
				}
				//Creation de l'objet Deck
				Deck deck = new Deck(cartesdeck.getString(7));
				deck.setCartes(cartes);
				decksj.add(deck);
			}
			
		}
	
		
		decksjoueur.close();
		this.decks = decksj;
		
	}

}
