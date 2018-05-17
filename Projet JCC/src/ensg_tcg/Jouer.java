package ensg_tcg;
/**
 * @author Parant Maxime, Beauvallet Clement
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;

public class Jouer {
	
	private Joueur gagnant;
	private Joueur perdant;
	private int scoreg;
	private int scorep;
	
	public Joueur getGagnant() {
		/**
		 * @return Joueur : vainqueur de la partie.
		 */
		return this.gagnant;
	}
	
	public int getScorej1() {
		/**
		 * @return int : score du vainqueur de la partie.
		 */
		return this.scoreg;
	}
	
	public int getScorej2() {
		/**
		 * @return int : score du perdant de la partie.
		 */
		return this.scorep;
	}
	
	public List<Joueur> jouer(Joueur joueur1, Joueur joueur2, Deck deck1, Deck deck2) {
		/**
		 * Methode organisant la structure de la partie (tour du joueur, comptage des points, determination du vainqueur).
		 * @param Joueur joueur1 : Joueur 1.
		 * @param Joueur joueur2 : Joueur 2.
		 * @param Deck deck1 : Deck du joueur 1.
		 * @param Deck deck2 : Deck du joueur 2.
		 * @return List<Joueur> : Liste contenant le Gagnant puis le Perdant.
		 */
		//Initialisation de la partie
	
		List<Carte> main_j1= new ArrayList<>();
		for(Carte c:deck1.getCartes()) {
			main_j1.add(c);
		}
		List<Carte> main_j2= new ArrayList<>();
		for(Carte c:deck2.getCartes()) {
			main_j2.add(c);
		}
		List<Carte> terrain1 = new ArrayList<>();
		for(int i=0; i<5; i++) {
			terrain1.add(null);
		}
		List<Carte> terrain2 = new ArrayList<>(5);
		for(int i=0; i<5; i++) {
			terrain2.add(null);
		}
	
		//Debut de la partie
	
		List<Integer> score1 = new ArrayList<>();
		List<Integer> score2 = new ArrayList<>();
		int tour=0;
		score1.add(0);
		score2.add(0);
		
		//5 tours des joueurs
		for (int t=1; t<=5; t++) {
			System.out.println("\n\nTour "+t);
			System.out.println("\n" + joueur1.getNom());
			score1.add(poser_carte(main_j1,terrain1, false) + score1.get(t-1));//pose de la carte joueur 1.
			System.out.println("\n" + joueur2.getNom());
			if (joueur2.getNom().equals("Ordinateur")){ //pose aleatoire de l'ordinateur.
				score2.add(poser_carte(main_j2,terrain2, true) + score2.get(t-1));
				
			}
			else {
				score2.add(poser_carte(main_j2,terrain2, false) + score2.get(t-1));//pose de la carte joueur 2.
			}
			
			System.out.println("\n" + joueur1.getNom() + ": " + score1.get(t) + "\t"+ joueur2.getNom()+ ": " + score2.get(t));
			tour+=1;
		
		}
	
	
		//determination du vainqueur
	
		while(gagnant == null & tour>0 ) {
	
			if (score1.get(tour)>score2.get(tour)) {
				this.gagnant = joueur1;
				this.scoreg = score1.get(5);
				this.perdant = joueur2;
				this.scorep = score2.get(5);
			}
			else if(score1.get(tour)<score2.get(tour)) {
				this.gagnant = joueur2;
				this.scoreg = score2.get(5);
				this.perdant = joueur1;
				this.scorep = score1.get(5);
			}
			tour-=1;
			
		}
		if (gagnant == null) {
			int a = (int) Math.random()*2;
			if (a==0) {
				this.gagnant=joueur1;
				this.scoreg = score1.get(5);
				this.perdant=joueur2;
				this.scorep = score2.get(5);
			}
			else {
				this.gagnant=joueur2;
				this.scoreg = score2.get(5);
				this.perdant=joueur1;
				this.scorep = score1.get(5);
			}
		}
		
		gagnant.setScore(scoreg);
		perdant.setScore(scorep);
		List<Joueur> resultat = new ArrayList<>();
		resultat.add(gagnant);
		resultat.add(perdant);
		System.out.println("\n" + gagnant.getNom() + " a gagné avec un score de " + gagnant.getScore() + " contre " + perdant.getScore() + "\n");
		return resultat;
	}

	



	public int poser_carte(List<Carte> main, List<Carte> terrain, boolean est_ordi) {
		/**
		 * Methode effectuant la pose de la carte a une position choisie.
		 * @param List<Carte> main : Main du joueur (liste des cartes restantes).
		 * @param List<Carte> terrain : Liste des position du terrain (carte si elle est prise, null si elle est libre).
		 * @param boolean est_ordi : Booleen indiquant si le joueur est un humain ou un ordinateur.
		 */
		Scanner sc = new Scanner(System.in);
		int e=0;
		int score = 0;
		Carte cartep = null;
		if(est_ordi == false) { //Si le joueur est humain, choix de la carte.
			System.out.print("Vos cartes : ");
			for (Carte c:main) {
				System.out.print(c.getNom() + ", ");
			}
			System.out.print("\nVotre carte : ");
			String nomcarte = sc.next();
	
			//3 essais pour saisir la bonne carte
		
			while (cartep == null & e<2) {
				e+=1;
				for (Carte c: main) {
			
					if (nomcarte.equals(c.getNom())) {
						cartep = c;
					}
				}
				if(cartep == null) {
					System.out.println("Vous n'avez pas cette carte");
					System.out.print("Votre carte : ");
					nomcarte = sc.next();
				}
			}
		}
		

		if (cartep == null) {// en cas de 3 erreur, carte choisie aleatoirement. Choix aleatoire direct pour l'ordinateur.
			double a=Math.random()*main.size();
			cartep = main.get((int) a); 
		}
		System.out.println("Carte choisie : " + cartep.getNom() );

		System.out.println("\nPositions libres : ");
		List<Integer> poslibr = new ArrayList<>();
		//Affichage des positions libres sur le terrain.
		for (int h=1; h<=terrain.size(); h++) {
			if (terrain.get(h-1)==null) {
				poslibr.add(h-1);
				System.out.print(h + ", ");
			}
	
		}
		
		
		int position = -1;
		if(est_ordi == false) {//Si le joueur est humain, choix de la position.
			e =0;
			while (position == -1 & e<=2){
				System.out.print("\nPosition : ");
				int pos = sc.nextInt();
				e+=1;
				for (int h=0; h<poslibr.size(); h++) {
					if(pos-1==poslibr.get(h)) {//Test si la position est disponnible.
						position = pos-1;
					}
				}
				if(position == -1) {
					System.out.println("Cette position n'est pas libre ou n'existe pas");
				}
			}
		}
		
		
		if (position==-1) {//au bout de 3 essais ou si ordinateur, poisition choisie aleatoirement.
			double a =Math.random()*poslibr.size();
			position = poslibr.get((int) a);
		}
		
		if(est_ordi == false) {
			System.out.println("Vous avez posé la carte " + cartep.getNom() + " à la position " + (position+1) + ".");
		}
		else {
			System.out.println("\nL'ordinateur a posé la carte " + cartep.getNom() + " à la position " + (position+1) + ".");
		}
		
		//Calcul du score
				score=cartep.getScore();
				for (int c = 0; c<5; c++) {
					if (terrain.get(c)!=null) {
						if(terrain.get(c).getElement()== cartep.getElement()) {
							score+=1;
						}
						if(terrain.get(c).getType()==cartep.getType()) {
							score+=1;
						}
					}
				}
				
		// Pose de la carte
		terrain.remove(position);
		terrain.add(position, cartep);
		main.remove(cartep);
		return score;
	}
}
