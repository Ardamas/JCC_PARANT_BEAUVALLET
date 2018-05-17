package ensg_tcg;
/**
 * @author Parant Maxime, Beauvallet Clement
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Carte {
	
	private int id;
	private String nom;
	private Rarete rarete;
	private int score;
	private Element element;
	private Type type;
	private Edition edition;
	private int annee;
	private boolean ouvert;
	
	
	public Carte(int id,Connection conn) throws SQLException {
		/**
		 * @param	int id : L'id de la carte qui va etre cree a partir de la database.
		 * @param	Connection conn : Connection a la database.
		 * Ce constructeur cree un objet carte a partir de son id , en recuperant toutes les donnees dans la database.
		 */
		
		this.id = id;
		PreparedStatement carte = conn.prepareStatement("SELECT * FROM cartes WHERE id = ?");
		carte.setInt(1, id);
		ResultSet donnee = carte.executeQuery();
		donnee.next();
		this.nom = donnee.getString(1);
		this.edition = Edition.valueOf(donnee.getString(3));
		this.annee = donnee.getInt(4);
		this.rarete = Rarete.valueOf(donnee.getString(5));
		this.score = donnee.getInt(6);
		this.type = Type.valueOf(donnee.getString(7));
		this.element= Element.valueOf(donnee.getString(8));
		this.ouvert= donnee.getBoolean(11);
		
	}
	
	public String getNom() {
		/**
		 * @return String : nom de la carte.
		 */
		return this.nom;
	}

	public int getScore() {
		/**
		 * @return int : score de la carte.
		 */
		return this.score;
	}
	
	public Element getElement(){
		/**
		 * @return Element : element de la carte.
		 */
		return this.element;
	}
	
	public Integer getId() {
		/**
		 * @return Integer : id de la carte.
		 */
		return this.id;
		
	}
	
	public Type getType() {
		/**
		 * @return Type : type de la carte.
		 */
		return this.type;
	}
	
	public Edition getEdition() {
		/**
		 * @return Integer : edition de la carte.
		 */
		return this.edition;
	}
	
	public void setScore(int valeur) {
		/**
		 * @param int valeur : la valeur de la carte a atrribuer.
		 * Cette methode change la valeur d'une carte. Elle est appelee en debut de tournoi pour attribuer les points supplementaires
		 * a la carte en fonction de ses similarites avec le type, l'element et l'edition que le tournoi met a l'honneur.
		 */
		this.score = valeur;
	}

}
