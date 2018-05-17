package ensg_tcg;
/**
 * @author Parant Maxime, Beauvallet Clement
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;

public class Tournoi {
	private Categorie categorie;
	private List<Joueur> joueurs;
	private Type type;
	private Element element;
	private Edition edition;
	
	public Tournoi(Categorie categorie, List<Joueur> joueurs, Type type, Element element, Edition edition){
		/**
		 * Construit l'objet Tournoi.
		 * @param Categorie categorie : Categorie du Tournoi (Championnat ou eliminatoire).
		 * @param List<Joueur> joueurs : Liste des joueurs participant au tournoi
		 * @param Type type : Type mis a l'honneur par le tournoi.
		 * @param Element element : Element mis a l'honneur par le tournoi.
		 * @param Edition edition : Edition mise a l'honneur par le tournoi.
		 */
		//gestion d'erreur nombre de joueurs (Potentiellement inutile etant donne l'utilisation de la methode dans le main).
		if (joueurs.size() == 8) {
			this.joueurs = joueurs;
			this.categorie = categorie;
			this.type = type;
			this.element = element;
			this.edition = edition;
		}
		else {
			System.out.println("Le nombre de joueurs n'est pas bon.");
		}
	}
	
	public List<Joueur> tournoi_elim(List<Joueur> joueurs) {
		/**
		 * Methode parametrant et organisant un tournoi eliminatoire.
		 * @param List<Joueur> joueurs : Joueurs participant au tournoi.
		 * @return List<Joueur> : Classement des joueurs a la fin du tournoi.
		 */
		Scanner sc = new Scanner(System.in);
		
		// Choix des decks pour les 8 joueurs.
		System.out.println("\n");
		for(int j=1;j<=8;j++) {
			Deck deck = null;
			for (int r=0; r<3; r++) {//Boucles pour choisir les decks du joueur (avec verification d'existence) : trois essais.
				System.out.print("\nDecks diponibles pour "+ joueurs.get(j-1).getNom() +": ");
				for (Deck d: joueurs.get(j-1).getDecks()) {
					System.out.print(d.getNom()+ ", ");
				}
				System.out.print("\nChoix du deck : ");
				String nomdeck1 = sc.nextLine();
				
				for (Deck d: joueurs.get(j-1).getDecks()) {
					if (nomdeck1.equals(d.getNom())) {
						deck = d;
						joueurs.get(j-1).setDeck(deck);
					}
				}
				if (nomdeck1.equals("Aleatoire")) {
					deck = joueurs.get(j-1).getDecks().get((int) Math.random()*joueurs.get(j-1).getDecks().size());
					joueurs.get(j-1).setDeck(deck);
				}
				
				if(deck == null) {
					System.out.println("Deck inconnu.");
				}
				else {
					break;
				}
				
			}
			if(deck == null) {
				double r =Math.random()*(joueurs.get(j-1).getDecks().size());
				deck = joueurs.get(j-1).getDecks().get((int) r );
				joueurs.get(j-1).setDeck(deck);
			}
			System.out.println("Deck choisi : " + deck.getNom() );
			
			//Ajout des points pour les cartes recevant les bonus du tournoi.
			for (Carte c : joueurs.get(j-1).getDeck().getCartes()) {
				if (c.getEdition() == this.edition) {
					c.setScore(c.getScore()+1);
				}
				if(c.getElement()== this.element) {
					c.setScore(c.getScore()+1);
				}
				if(c.getType() == this.type) {
					c.setScore(c.getScore()+1);
				}
			}
		}
		
		
		
		
		
		
		//Lancement des parties suivant le tableau eliminatoire.
		Jouer jeu = new Jouer();
		System.out.println("\n\nQuart de finale 1");
		List<Joueur> quart1 = jeu.jouer(joueurs.get(0), joueurs.get(1), joueurs.get(0).getDeck(), joueurs.get(1).getDeck() );
		System.out.println("\n\nQuart de finale 2");
		List<Joueur> quart2 = jeu.jouer(joueurs.get(2), joueurs.get(3), joueurs.get(2).getDeck(), joueurs.get(3).getDeck() );
		System.out.println("\n\nQuart de finale 3");
		List<Joueur> quart3 = jeu.jouer(joueurs.get(4), joueurs.get(5), joueurs.get(4).getDeck(), joueurs.get(5).getDeck() );
		System.out.println("\n\nQuart de finale 4");
		List<Joueur> quart4 = jeu.jouer(joueurs.get(6), joueurs.get(7), joueurs.get(6).getDeck(), joueurs.get(7).getDeck() );
		System.out.println("\n\nDemi-finale des perdants 1");
		List<Joueur> demiperd1 = jeu.jouer(quart1.get(1), quart2.get(1), quart1.get(1).getDeck(), quart2.get(1).getDeck() );
		System.out.println("\n\nDemi-finale des perdants 2");
		List<Joueur> demiperd2 = jeu.jouer(quart3.get(1), quart4.get(1), quart3.get(1).getDeck(), quart4.get(1).getDeck() );
		System.out.println("\n\nDemi-finale des vainqueurs 1");
		List<Joueur> demigagn1 = jeu.jouer(quart1.get(0), quart2.get(0), quart1.get(0).getDeck(), quart2.get(0).getDeck() );
		System.out.println("\n\nDemi-finale des vainqueurs 2");
		List<Joueur> demigagn2 = jeu.jouer(quart3.get(0), quart4.get(0), quart3.get(0).getDeck(), quart4.get(0).getDeck() );
		System.out.println("\n\n7e place");
		List<Joueur> place7 = jeu.jouer(demiperd1.get(1), demiperd2.get(1), demiperd1.get(1).getDeck(), demiperd2.get(1).getDeck() );
		System.out.println("\n\n5e place");
		List<Joueur> place5 = jeu.jouer(demiperd1.get(0), demiperd2.get(0), demiperd1.get(0).getDeck(), demiperd2.get(0).getDeck() );
		System.out.println("\n\n3e place");
		List<Joueur> place3 = jeu.jouer(demigagn1.get(1), demigagn2.get(1), demigagn1.get(1).getDeck(), demigagn2.get(1).getDeck() );
		System.out.println("\n\nFinale");
		List<Joueur> finale = jeu.jouer(demigagn1.get(0), demigagn2.get(0), demigagn1.get(0).getDeck(), demigagn2.get(0).getDeck() );
		
		//Creation du classement des joueurs.
		List<Joueur> classement = new ArrayList<>();
		classement.add(finale.get(0));
		classement.add(finale.get(1));
		classement.add(place3.get(0));
		classement.add(place3.get(1));
		classement.add(place5.get(0));
		classement.add(place5.get(1));
		classement.add(place7.get(0));
		classement.add(place7.get(1));			
		return classement;
		
	}
	
	public List<Joueur> tournoi_champ(List<Joueur> joueurs){
		/**
		 * Methode parametrant et organisant un championnat.
		 * @param List<Joueur> joueurs : Joueurs participant au championnat.
		 * @return List<Joueur> : Classement des joueurs a la fin du championnat.
		 */
		Scanner sc = new Scanner(System.in);
		// Choix des decks pour les 8 joueurs.
		System.out.println("\n");
		for(int j=1;j<=8;j++) {
			Deck deck = null;
			for (int r=0; r<3; r++) {//Boucles pour choisir les decks du joueur (avec verification d'existence) : trois essais.
				System.out.print("\nDecks diponibles pour "+ joueurs.get(j-1).getNom() +": ");
				for (Deck d: joueurs.get(j-1).getDecks()) {
					System.out.print(d.getNom()+ ", ");
				}
				System.out.print("\nChoix du deck : ");
				String nomdeck1 = sc.nextLine();
				
				for (Deck d: joueurs.get(j-1).getDecks()) {
					if (nomdeck1.equals(d.getNom())) {
						deck = d;
						joueurs.get(j-1).setDeck(deck);
					}
				}
				if (nomdeck1.equals("Aleatoire")) {
					deck = joueurs.get(j-1).getDecks().get((int) Math.random()*joueurs.get(j-1).getDecks().size());
					joueurs.get(j-1).setDeck(deck);
				}
				
				if(deck == null) {
					System.out.println("Deck inconnu.");
				}
				else {
					break;
				}
			}
			if(deck == null) {
				double r = Math.random()*(joueurs.get(j-1).getDecks().size());
				deck = joueurs.get(j-1).getDecks().get((int) r);
				joueurs.get(j-1).setDeck(deck);
			}
			System.out.println("Deck choisi : " + deck.getNom() );
		}
		int r = 1;
		//Enchainement des parties au sein du tournoi en creant toutes les rencontres possibles.
		for(int e=1; e<8; e++) {
			for(int f=r; f<=7; f++) {
				Jouer jeu = new Jouer();
				List<Joueur> match = jeu.jouer(joueurs.get(e-1), joueurs.get(f), joueurs.get(e-1).getDeck(), joueurs.get(f).getDeck() );
				match.get(0).setPointschamp(1);
			}
			r+=1;
		}
		
		//Creation du classement des joueurs.
		List<Joueur> classement = new ArrayList<>();
		Joueur mj = joueurs.get(0);
		for(int i=0; i<7; i++) {
			for(Joueur j: joueurs) {
				if(mj.getPointschamp()<j.getPointschamp()) {
					mj = j;
				}
			}
			classement.add(mj);
			joueurs.remove(mj);
			mj = joueurs.get(0);
		}
		classement.add(joueurs.get(0));
		return classement;
	}
}
