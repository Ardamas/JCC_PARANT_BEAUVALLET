package ensg_tcg;
/**
 * @author Parant Maxime, Beauvallet Clement
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	static final String DB_URL = "jdbc:postgresql://localhost:5432/projet JCC";
	static final String USER = "postgres";
	static final String PASS = "dhknvpmcsv";
	

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		
		//Connection a la database
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver"); 
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connexion etablie avec succes !");
		}
		catch(Exception e){
			e.printStackTrace(); 
		
		}
		finally{
			if(conn != null){
				conn.close(); // toujours fermer les differentes ressources quand il n’y en as plus besoin
				}
			}
	
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Bienvenue dans ENSG the Card Game");
		String a = "";
		
		//menu du jeu.
		while (! a.equals("Quitter")) {
			System.out.println("\n\nJouer, Collection, Regles, Quitter");
			a = sc.next();
			if (a.equals("Jouer")) {
				while (! a.equals("Retour")) {
					System.out.println("\nMode de jeu :");
					System.out.println("Partie , Tournoi, Retour");
					a = sc.next();
					if (a.equals("Partie")) {
						while (! a.equals("Retour")) {
							System.out.println("\nFormat de Partie : Ouvert, Draft\nRetour");
							a=sc.next();
							if(a.equals("Ouvert")) {
								while (! a.equals("Retour")) {
									//Parametrage de la partie ouverte.
									System.out.println("\nJoueur1 : ");
									String j=sc.next();
									conn = DriverManager.getConnection(DB_URL, USER, PASS);
									Statement state = conn.createStatement();
									ResultSet listJoueurs = state.executeQuery("SELECT nom FROM joueurs");
									boolean ok = false;
									while (listJoueurs.next()) { //verification si le joueur existe.
										if (listJoueurs.getString(1).equals(j)) {
											ok=true;
										}
									}
									if (ok==false) {
										System.out.println("Cejoueur n'existe pas");
										break;
									}
									Joueur joueur1 = new Joueur(j);
									joueur1.setDecks(conn, j);
									if (joueur1.getDecks().size()==0) {//verification si le joueur a au moins un deck.
										System.out.println("Ce joueur n'a pas de decks.");
										a="Retour";
										break;
									}
									System.out.println("Joueur2 : (Ordi ou Nom)");
									j = sc.next();
									
									if(j.equals("Ordi")) {//Creation d'un joueur ordinateur (deck aleatoire).
										Deck deckordi = new Deck("Deckordi");
										deckordi.setDeckalea(conn, 6);
										Joueur ordinateur = new Joueur("Ordinateur");
										ordinateur.setDeck(deckordi);
										Partie partie = new Partie(joueur1, ordinateur, Format.Ouvert);
										partie.lancer_ouvert();
																				
									}
									else {//memes operation qu'avec le joueur 1 si le joueur 2 n'est pas un ordiateur.
										listJoueurs = state.executeQuery("SELECT nom FROM joueurs");
										ok = false;
										while (listJoueurs.next()) {
											if (listJoueurs.getString(1).equals(j)) {
												ok=true;
											}
										}
										if (ok==false) {
											System.out.println("Ce joueur n'existe pas");
											break;
										}
										
										Joueur joueur2 = new Joueur(j);
										joueur2.setDecks(conn, j);
										if (joueur2.getDecks().size()==0) {
											System.out.println("Ce joueur n'a pas de decks.");
											a="Retour";
											break;
										}
										Partie partie = new Partie(joueur1, joueur2, Format.Ouvert);
										partie.lancer_ouvert();
									}
									a = "Retour";
								}
								
							}
							
							
							else if(a.equals("Draft")) {
								//Parametrage de la partie draft.
								System.out.println("\nJoueur1 : ");
								String j=sc.next();
								conn = DriverManager.getConnection(DB_URL, USER, PASS);
								Joueur joueur1 = new Joueur(j);
								System.out.println("Joueur2 : ");
								j = sc.next();
								/**
								 * Draft contre un ordi non implemente finalement
								if(j.equals("Ordi")) {
									Joueur ordinateur = new Joueur("Ordinateur");
									Partie partie = new Partie(joueur1, ordinateur, Format.Draft);
									partie.lancer_draft(conn);							
								}
								*/
								
								Joueur joueur2 = new Joueur(j);
								Partie partie = new Partie(joueur1, joueur2, Format.Draft);
								partie.lancer_draft(conn);
									
							}
						}
					}
					
					else if(a.equals("Tournoi")) {
						while (! a.equals("Retour")) {
							//Parametrage du Tournoi.
							System.out.println("\nCategorie de Tournois : Championnat    Eliminatoire\nRetour");
							a = sc.next();
							if (!a.equals("Eliminatoire") & !a.equals("Championnat") ) {
								break;
							}
							//Choix de l'element, du type et de l'edition mise a l'honneur par le tournoi.
							System.out.println("\nElement du tournoi: ");
							String elemstr = sc.next();
							try { //verification de l'existence de l'element.
								Element.valueOf(elemstr);
							}
							catch(Exception e) {
								System.out.println("Cet element n'existe pas.");
								break;
							}
							Element element = Element.valueOf(elemstr);
							System.out.println("Type du tournoi: ");
							String typestr = sc.next();
							try {//verification de l'existence du type.
								Type.valueOf(typestr);
							}
							catch(Exception e) {
								System.out.println("Ce type n'existe pas.");
								break;
							}
							Type type = Type.valueOf(typestr);
							System.out.println("Edition du tournoi: ");
							String edistr = sc.next();
							try {//verification de l'existence de l'edition.
								Edition.valueOf(edistr);
							}
							catch(Exception e) {
								System.out.println("Cette edition n'existe pas.");
								break;
							}
							Edition edition = Edition.valueOf(edistr);
							
							List<Joueur> joueurs = new ArrayList<Joueur>();
							// Selection des 8 joueurs participant au tournoi.
							System.out.println("\n");
							for (int j = 1; j<=8; j++) {
								boolean ok = false;
								while(ok == false) {
									ok = true;
									System.out.println("Nom joueur "+ j +": ");
									String nom= sc.next();
									Joueur joueur = new Joueur(nom);
									conn = DriverManager.getConnection(DB_URL, USER, PASS);
									PreparedStatement existence = conn.prepareStatement("SELECT id FROM joueurs WHERE nom = ?");
									existence.setString(1, nom);
									ResultSet existencexe = existence.executeQuery();
									int i=0;
									while (existencexe.next()){
										i+=1;
										
									}
									if (i==0) {
										System.out.println("Ce joueur n'existe pas.");
										ok = false;
									}
									
									else {
										joueur.setDecks(conn, nom);
										if (joueur.getDecks().size()==0) {
											System.out.println("Ce joueur n'a pas de decks.");
											a="Retour";
											break;
										}
										joueurs.add(joueur);
									}
									
								}
								
								
								
							}
							List<Joueur> classement = new ArrayList<>();
							if(a.equals("Eliminatoire")) {
								//Lancement du tournoi en eliminatoire.
								Tournoi tournoi = new Tournoi(Categorie.Eliminatoire, joueurs, type, element, edition);
								classement = tournoi.tournoi_elim(joueurs);
							}
							else if(a.equals("Championnat")) {
								//lancement du tournoi en championnat.
								Tournoi tournoi = new Tournoi(Categorie.Championnat, joueurs, type, element, edition);
								classement = tournoi.tournoi_champ(joueurs);
								}
							//Affichage du classement des joueurs.
							System.out.println("\n\nClassement : ");
							int p=0;
							for(Joueur i:classement) {
								p+=1;
								System.out.print(p + " " + i.getNom() + " Avec les cartes: ");
								for (Carte c:i.getDeck().getCartes()) {
									System.out.print(c.getNom() + ", ");
								}
								System.out.println("");
							}
							a= "Retour";
						}
					}
				}
			}
			
			else if (a.equals("Collection")) {
				while (! a.equals("Retour")) {
					//Menu de collection.
					System.out.println("Choisissez votre joueur : ");
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection(DB_URL, USER, PASS);
					Statement state = conn.createStatement();
					ResultSet result = state.executeQuery("SELECT * FROM joueurs ORDER BY id ASC");
					ResultSetMetaData resultMeta = result.getMetaData();
					for(int i = 1; i <= resultMeta.getColumnCount(); i++){ //Affichage des colones.
						System.out.print("\t" + resultMeta.getColumnName(i).toUpperCase() + "\t *");
					}
					System.out.println("");
					int idj = 0;
					//Affichage des joueurs et de leurs decks.
					while(result.next()){ 
						for(int i = 1; i <= resultMeta.getColumnCount(); i++){ 
							System.out.print("\t" + result.getString(i) + "\t |");
						}
						System.out.println("\n\t------------------------------------------------------------------------------------------");
						idj+=1;
					}
					System.out.println("\tNouveau\t |\tRetour\t |");
					a= sc.next();
					if (a.equals("Nouveau")) {
						//Creation d'un nouveau joueur.
						System.out.print("Votre nom : ");
						a = sc.next();
						PreparedStatement pstate = conn.prepareStatement("INSERT INTO joueurs(nom, id) VALUES(?, ?)");
						pstate.setString(1, a);
						pstate.setInt(2, idj+1);
						pstate.executeUpdate();
						System.out.println("Joueur " + a + " créé");
					}
					else {
						result = state.executeQuery("SELECT * FROM joueurs ORDER BY id ASC");
						while(result.next()) {
							if (a.equals(result.getString(1))) {//test si le joueur existe.
								System.out.println("\nCreer, Supprimer, Retour (Menu)");
								String b=sc.next();
								if (b.equals("Creer")) {	
									//Creation d'un nouveau deck.
									PreparedStatement newdeck = conn.prepareStatement("INSERT INTO deck(nom,id,carte1,carte2,carte3,carte4,carte5,carte6,proprietaire) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
									System.out.println("\nNom du Deck :");
									String c = sc.next();
									newdeck.setString(1, c);
									//Recuperation de l'id du futur deck a partir de celui du dernier cree.
									ResultSet iddeck = state.executeQuery("SELECT id FROM deck ORDER BY id DESC LIMIT 1");
									boolean ok = false;
									while (iddeck.next()) {
										newdeck.setInt(2, iddeck.getInt(1)+1);
										ok = true;
									};
									if(ok ==false) {//Si c'est le premier deck cree : l'id est de 1.
										newdeck.setInt(2, 1);
									}
									
									//Selection des cartes.
									List<String> cartesajoutees = new ArrayList<>();
									for (int d = 1; d<= 6; d++) {
										ok = false;
										PreparedStatement carte = conn.prepareStatement("Select nom From cartes Where nom= ? ");
										while (ok==false) {//boucle jusqu'a ce que la carte choisie soit correcte.
											System.out.println("Carte" + d + ": ");
											String e= sc.next();
											while(ok==false) {
												ok=true;
												for(String h : cartesajoutees) {
													if (e.equals(h)) { //Test si la carte a deja ete prise (evite les doublons).
														ok=false;
														System.out.println("Cette carte est déjà dans le deck.");
														System.out.println("Carte" + d + ": ");
														e= sc.next();
													}
												}
												
											
											}
											cartesajoutees.add(e);	
											carte.setString(1, e);
											ResultSet cartexe = carte.executeQuery();
											cartexe.next();
											try {
												//verification de l'existence de la carte.
												newdeck.setString(d+2, cartexe.getString(1));
											}
											catch(Exception error){
												ok=false;
												System.out.println("Cette carte n'existe pas.");
												cartesajoutees.remove(e);
											}
											if (ok==true) {//Si tous les test precedents sont passes, verification que la carte est disponnible en mode ouvert.
												PreparedStatement ouvert = conn.prepareStatement("SELECT ouvert FROM cartes WHERE nom = ?");
												ouvert.setString(1, e);
												ResultSet ouvertexe = ouvert.executeQuery();
												ouvertexe.next();
												if(ouvertexe.getBoolean(1) == false) {
													ok=false;
													System.out.println("Cette carte n'est pas disponible en mode ouvert.");
													cartesajoutees.remove(e);
												}
											}
										}
										//Mise a jour de la requête preparee.
										ResultSet cartexe = carte.executeQuery();
										cartexe.next();
										newdeck.setString(d+2, cartexe.getString(1));
									}
									
									newdeck.setString(9, a);
									newdeck.executeUpdate();//ecriture du nouveau deck dans la table deck.
									//Ecriture du nouveau deck dans la table joueur.
									PreparedStatement jdeck = conn.prepareStatement("UPDATE joueurs SET deck1 = ?, deck2 = ?, deck3 = ?, deck4 = ? WHERE nom = ?");
									jdeck.setString(5, a);
									PreparedStatement lignej = conn.prepareStatement("Select * From joueurs Where nom= ? ");
									lignej.setString(1, a);
									ResultSet lignejexe= lignej.executeQuery();
									lignejexe.next();
									int k =1;
									ok = false;
									while (k<=4) { //Recherche d'un emplacement de deck libre.
										
										String nomdeck = lignejexe.getString(k+2);
										if(! lignejexe.wasNull() | ok==true) {
											jdeck.setString(k, nomdeck);
											
											
										}
										else {
											jdeck.setString(k, c);
											ok = true;
											
										}
										k+=1;
										
									}
									jdeck.executeUpdate();//ecriture.
								}
								else if(b.equals("Supprimer")){
									//suppression d'un deck.
									System.out.println("\nDeck à Supprimer?");
									PreparedStatement decks = conn.prepareStatement("SELECT nom FROM deck WHERE deck.proprietaire = ?");
									decks.setString(1, a);
									ResultSet deck = decks.executeQuery();
									while(deck.next()) {
										System.out.print(deck.getString(1) + "\t"); //affichage des decks possibles a supprimer.
									}
									decks.close();
									String d=sc.next();//nom du deck.
									PreparedStatement suppression = conn.prepareStatement("DELETE FROM deck WHERE deck.nom = ?");
									suppression.setString(1,d);
									suppression.executeUpdate(); //Suppression du deck de la table deck.
									PreparedStatement jdeck = conn.prepareStatement("UPDATE joueurs SET deck1=?, deck2=?, deck3=?, deck4=? WHERE nom=?");
									jdeck.setString(5, a);
									PreparedStatement lignej = conn.prepareStatement("Select * From joueurs Where nom= ? ");
									lignej.setString(1, a);
									ResultSet lignejexe= lignej.executeQuery();
									lignejexe.next(); 
									//Recuperation des decks du joueur.
									for (int i=1; i<=4;i++) {
										
										if (d.equals(lignejexe.getString(i+2))) {
											jdeck.setString(i, null);
										}
										else {
											jdeck.setString(i, lignejexe.getString(i+2));
										}
									}
									
									jdeck.executeUpdate();//Suppression du deck de la table joueurs. 
								}
								break;
							}
							
						}
					}
					result.close();
					state.close();
					a= "Retour";
				}
				
			}
			
			else if (a.equals("Regles")) {
				//Affichage des regles du jeu.
				System.out.println("- Au cours d'une partie, chaque joueur pose tour a tour une carte qui lui accordera des points");
				System.out.println("en fonction de sa rarete.");
				System.out.println("- En plus des points de rarete, pour chaque carte du meme element et du meme type deja posee sur");
				System.out.println("le plateau, 1 point supplementaire sera accorde par similarite.");
				System.out.println("- Le joueur qui a le plus de points a la fin de la partie gagne.");
				System.out.println("- Il existe plusieurs modes de jeu:");
				System.out.println("- En partie ouverte, chaque joueur choisit son deck qu'il aura au prealable cree via le menu");
				System.out.println("Collection.");
				System.out.println("- En partie Draft, deux sets de 7 cartes sont crees aleatoirement, et les joueur piochent 6");
				System.out.println("cartes tour a tour en s'echangeant les paquets a chaque nouvelle carte, constituant ainsi leur");
				System.out.println("deck pour la partie.");
				System.out.println("- En mode Tournoi, 8 joueurs s'affrontent suivant un tableau eliminatoire ou en championnat");
				System.out.println("avec un deck unique qu'il choisiront avant de commencer les matchs.");
				System.out.println("- De plus, chaque tournoi met a l'honneur un type, un element et une edition. Chaque carte");
				System.out.println("posee ayant un attribut correspondant apportera 1 point supplementaire par similarite.");
				System.out.println("");
			}
		}

	}

}
