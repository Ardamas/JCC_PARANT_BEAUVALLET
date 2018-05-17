package ensg_tcg;
/**
 * 
 * @author Parant Maxime, Beauvallet Clement
 *
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Partie {
	private Joueur joueur1;
	private Joueur joueur2;
	private Format format;
	private Joueur gagnant;
	private Joueur perdant;
	private int scoreg;
	private int scorep;
	
	public Partie(Joueur joueur1, Joueur joueur2, Format format) {
		/**
		 * @param Joueur joueur1 : Le joueur 1 qui joue la partie.
		 * @param Joueur joueur2 : Le joueur 2 qui joue la partie.
		 * @param Format format : Le format de la partie (Ouvert ou Draft).
		 * Constructeur creant l'objet partie qui initialise le match.
		 */
		this.joueur1 = joueur1;
		this.joueur2 = joueur2;
		this.format = format;
		
	}
	
	public Joueur getGagnant() {
		/**
		 * @return Joueur : vainqueur d'une partie.
		 */
		return this.gagnant;
	}
	
	public Joueur getPerdant() {
		/**
		 * @return Joueur : perdant d'une partie.
		 */
		return this.perdant;
	}
	
	public int getScoreg() {
		/**
		 * @return int : score du vainqueur d'une partie.
		 */
		return this.scoreg;
	}
	
	public int getScorep() {
		/**
		 * @return int : score du perdant d'une partie.
		 */
		return this.scorep;
	}
	
	public void lancer_ouvert() {
		/**
		 * Cette methode Initialise une partie en format Ouvert. Elle permet aux joueurs de choisir leur deck et recupere
		 * le gagnant et le perdant une fois la partie jouee.
		 */
		Scanner sc = new Scanner(System.in);
		Deck deck1 = null;
		Deck deck2 = null;
		
		for (int r=0; r<3; r++) {//Boucles pour choisir le deck du joueur 1 (avec verification d'existence) : trois essais.
			System.out.print("\nDecks diponibles pour "+ joueur1.getNom() +": ");
			for (Deck d: joueur1.getDecks()) {
				System.out.print(d.getNom()+ ", ");
			}
			System.out.print("\nChoix du deck : ");
			String nomdeck1 = sc.nextLine();
			
			for (Deck d: joueur1.getDecks()) {
				if (nomdeck1.equals(d.getNom())) {
					deck1 = d;
				}
			}
			if (nomdeck1.equals("Aleatoire")) {
				deck1 = joueur1.getDecks().get((int) Math.random()*joueur1.getDecks().size());
			}
			
			if(deck1 == null) {
				System.out.println("Deck inconnu.");
			}
			else {
				break;
			}
		}
		
		if(deck1 == null) { //Si le deck n'a pas ete correctement choisi au bout de trois essais, le choisi aleatoirement.
			double a=Math.random()*joueur1.getDecks().size();
			deck1 = joueur1.getDecks().get((int) a);
		}
		System.out.println("Deck choisi : " + deck1.getNom() );
		
		
		if (joueur2.getNom().equals("Ordinateur")) { //Test si le joueur est un ordinateur.
			deck2 = joueur2.getDeck(); // Recuperation du Deck pre-genere aleatoirement pour l'ordinateur.
		}
		else {
			for (int r=0; r<3; r++) {//Boucles pour choisir le deck du joueur 2 s'il est humain (avec verification d'existence) : trois essais.
				System.out.print("Decks diponibles pour "+ joueur2.getNom() + ": ");
				for (Deck d: joueur2.getDecks()) {
					System.out.print(d.getNom()+ ", ");
				}
				System.out.print("\nChoix du deck: ");
				String nomdeck2 = sc.nextLine();
				
				for (Deck d: joueur2.getDecks()) {
					if (nomdeck2.equals(d.getNom())) {
						deck2 = d;
					}
				}
				if (nomdeck2.equals("Aleatoire")) {
					deck2 = joueur2.getDecks().get((int) Math.random()*joueur2.getDecks().size());
				}
				
				if(deck2 == null) {
					System.out.println("Deck inconnu.");
				}
				else {
					break;
				}
			}
			if(deck2 == null) {//Si le deck n'a pas ete correctement choisi au bout de trois essais, le choisi aleatoirement.
				double a=Math.random()*joueur2.getDecks().size();
				deck2 = joueur2.getDecks().get((int) a);
			}
			System.out.println("Deck choisi : " + deck2.getNom() );
			
		}
		
		Jouer jeu = new Jouer(); 
		
		List<Joueur> joueurs = jeu.jouer(joueur1, joueur2, deck1, deck2); //lancement du jeu a proprement parler.
		//recuperation des gagnant et perdant et de leur score respectifs.
		gagnant = joueurs.get(0);
		scoreg = joueurs.get(0).getScore();
		perdant = joueurs.get(1);
		scorep = joueurs.get(1).getScore();
		
	}
	
	public void lancer_draft(Connection conn) throws SQLException {
		/**
		 * Cette methode Initialise une partie en format Draft. Elle permet aux joueurs de creer leur deck a partir de deux pioches
		 * generees aleatoirement puis recupere le gagnant et le perdant ainsi que leur score respectif une fois la partie jouee.
		 * @param Connection conn : connection a la database pour creer les pioches en prenant les cartes aleatoirement dans
		 * la database.
		 */
		
		//creation de pioches aleatoires.
		Deck draft1 = new Deck("draft1");
		draft1.setDeckalea(conn, 7); 
		List<Carte> d1 = draft1.getCartes();
		Deck draft2 = new Deck("draft2");
		draft2.setDeckalea(conn, 7);
		List<Carte> d2 = draft2.getCartes();
		List<Carte> cartes1 = new ArrayList<>();
		List<Carte> cartes2 = new ArrayList<>();
		int d = 1;
		for (int i=1; i<=6; i++) { // Operations de choix des cartes dans les pioches par les joueurs (en accord avec les consignes du sujet).
			for (int j = 1; j<=2; j++) {
				boolean ok = false;
				System.out.print("\nJoueur " + j +": \nChoisissez une carte : ");
				List<Carte> draft = null;
				
				if(d==1) {
					draft = d1;
				}
				else {
					draft = d2;
				}
				for (int t = 0 ; t<3; t++) {
					for (Carte l:draft) {
						System.out.print(l.getNom() + ", ");
					}
					System.out.println("");
					Scanner sc = new Scanner(System.in);
					String cartechoix = sc.next();
					
					if (cartechoix.equals("Aleatoire")) {
						ok = true;
						int c=(int) Math.random()*draft.size();
						if(j==1) {
							cartes1.add(draft.get(c));
							if(d==1) {
								d1.remove(draft.get(c));
							}
							else {
								d2.remove(draft.get(c));
							}
						}
						else {
							cartes2.add(draft.get(c));
							if(d==1) {
								d1.remove(draft.get(c));
							}
							else {
								d2.remove(draft.get(c));
							}
						}
					}
					else {
						for (Carte c: draft) {
							if (cartechoix.equals(c.getNom())) {
								ok = true;
								if(j==1) {
									cartes1.add(c);
									if(d==1) {
										d1.remove(c);
									}
									else {
										d2.remove(c);
									}
								}
								else {
									cartes2.add(c);
									if(d==1) {
										d1.remove(c);
									}
									else {
										d2.remove(c);
									}
								}
								break;
							}
						}
					}
					if (ok == true) {
						break;
					}
				}
				if(ok == false) {
					
					if(j==1) {
						cartes1.add(draft.get((int) Math.random()*draft.size()));
					}
					else {
						cartes2.add(draft.get((int) Math.random()*draft.size()));
					}
				}
				if(j==1) {
					System.out.println("Carte choisie : " + cartes1.get(i-1).getNom());
				}
				else {
					System.out.println("Carte choisie : " + cartes2.get(i-1).getNom());
				}
			
				if(j==1) { //changement de pioche une fois que le joueur 1 a pioche. (Equivaut aux deux joueurs qui s'echangent
						   //les pioches une fois que le joueur 2 a pioche).
					if(d==1) {
						d=2;
					}
					else {
						d=1;
					}
				}
				
			}
		}
		//generation des decks des deux joueurs a partir des cartes piochees.
		Deck deck1 = new Deck("deck1");
		deck1.setCartes(cartes1);
		Deck deck2 = new Deck("deck2");
		deck2.setCartes(cartes2);
		Jouer jeu = new Jouer();
		List<Joueur> joueurs = jeu.jouer(joueur1, joueur2, deck1, deck2); //lancement du jeu a proprement parler
		//recuperation des gagnant et perdant et de leur score respectif.
		gagnant = joueurs.get(0);
		scoreg = joueurs.get(0).getScore();
		perdant = joueurs.get(1);
		scorep = joueurs.get(1).getScore();
	}
}
