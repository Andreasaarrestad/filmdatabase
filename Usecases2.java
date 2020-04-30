package Filmdatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.mysql.cj.jdbc.result.ResultSetMetaData; 

public class Usecases2 extends DBConn {
	
	public Usecases2() {
		super();
        connect();
        
        try {
            conn.setAutoCommit(false);
        } 
        catch (SQLException e) {
            System.out.println("Feil  under tilkobling til databasen: " + e);
            return;
        }
	}
	
	public void getRoles(int PersonID) {
		try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select Rolle from skuespillerifilm where PersonID = "+PersonID);
            conn.commit();
            
            while (rs.next()) {
                System.out.println(rs.getString("Rolle"));
                
            }

        } catch (Exception e) {
            System.out.println("db error = "+e);
            return;
        }
		
	}
	

	private void getFilm(int PersonID) {
		try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select Tittel from episodeorfilm where (select FilmID from skuespillerifilm where PersonID = "+PersonID+")");
            conn.commit();
            
            while (rs.next()) {
                System.out.println(rs.getString("Tittel"));
                
            }

        } catch (Exception e) {
            System.out.println("db error = "+e);
            return;
        }
		
	}
	
	public void getCompaniesByGenre() {
		try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select selskapID, Navn as Filmselskap, sjangerID, sjangerNavn, count(sjangerID) as Antall from episodeorfilm natural join filmutgiver natural join selskap natural join sjangerforfilm natural join sjanger group by sjangerID, selskapID");
            conn.commit();
            
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print("   ");
                System.out.print(rsmd.getColumnName(i));
            }
            
            System.out.println("\n");
            
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print("   ");
                    String columnValue = rs.getString(i);
                    
                    System.out.print(columnValue);
                }
                System.out.println("");
            }

        } catch (Exception e) {
            System.out.println("db error = "+e);
            return;
        }
	}

	
	public int InsertMovie(String tittel, int lengde, int utgivelsesar, String lanseringsdato, String beskrivelse, int utgittpavideo, String medium) {
		try {
			
            Statement stmt = this.conn.createStatement();
            stmt.execute("INSERT INTO episodeorfilm (FilmID, Tittel, Lengde, "
            		+ "Utgivelsesår, Lanseringsdato, Beskrivelse, UtgittPåVideo, LagetFor)"
            		+ " VALUES (DEFAULT,'"+tittel+"',"+lengde+","+utgivelsesar+",'"+lanseringsdato+"','"
            		+beskrivelse+"',"+utgittpavideo+",'"+medium+"')");
			
            conn.commit();
            
            
            //Henter ut den auto-incrementa filmID-en til filmen som akkurat ble lagt til
            Statement stmt2 = this.conn.createStatement();

            ResultSet rs = stmt2.executeQuery("SELECT filmID AS LastID FROM episodeorfilm WHERE filmID = @@Identity");
			
            conn.commit();
            
            
            rs.next();
            
            return Integer.parseInt(rs.getString("LastID"));
            
        } 
		catch (Exception e) {
            System.out.println("db error i InsertMovie = "+e);
            return 0;
        }	
	}
	
	public void newActor(int personID, String stilling, int FilmID, String rolle) {
		try {
			 Statement stmt = this.conn.createStatement();
	         stmt.execute("INSERT INTO "+stilling+"IFilm (FilmID, PersonID, rolle) VALUES ("+FilmID+","+personID+",'"+rolle+"')");
				
	         conn.commit();
	     
			} 
			catch (Exception e) {
	         System.out.println("db error i NewActor() = "+e);
	         return;
			}
	}
	
	public void newNotActor(int personID, String stilling, int FilmID) {
		try {
		 Statement stmt = this.conn.createStatement();
         stmt.execute("INSERT INTO "+stilling+"IFilm (FilmID, PersonID) VALUES ("+FilmID+","+personID+")");
			
         conn.commit();
     
		} 
		catch (Exception e) {
         System.out.println("db error i NewActor() = "+e);
         return;
		}	
		
	}
	
	public void newAnmeldelse(String anmeldelseValg, int brukerID, int FilmID, String Kommentar, int Rating) {
		try {
			 Statement stmt = this.conn.createStatement();
	         stmt.execute("INSERT INTO "+anmeldelseValg+" (BrukerID, FilmID, Kommentar, Rating) VALUES ("+brukerID+","+FilmID+",'"+Kommentar+"',"+Rating+")");
				
	         conn.commit();
	     
			} 
			catch (Exception e) {
	         System.out.println("db error i newAnmeldelse() = "+e);
	         return;
			}	
	}
	
	public void addBruker(String brukernavn) {
		try {
			 Statement stmt = this.conn.createStatement();
	         stmt.execute("INSERT INTO bruker (BrukerID, Brukernavn) VALUES (DEFAULT,'"+brukernavn+"')");
				
	         conn.commit();
	     
			} 
			catch (Exception e) {
	         System.out.println("db error i newBruker() = "+e);
	         return;
			}
		
	}
	
	
	public static void main(String[] args) {
		Usecases2 usecase_ = new Usecases2();
		
		Scanner myobj = new Scanner(System.in);
		while(true) {
		 
		 //Hva vil du gjøre?
		 System.out.println("Trykk 1 for å gi navn på alle rollene til en skuespiller,"
		 		+ " 2 for hvilke filmer en skuespiller har vært i,"
		 		+ " 3 for å finne filmselskap som lager flest filmer i hver sjanger,"
		 		+ " 4 for å sette inn ny film med regissør, manusforfattere, skuespillere og det som hører med,"
		 		+ " 5 for å sette inn en ny anmeldelse av en episode av en serie."); 
	
		 
		 int valg = myobj.nextInt();
		 
		 switch(valg) {
		 	case 1:
		 		 System.out.println("Skriv inn personID på skuespiller:");
		 		 myobj.nextLine();
		 		 
		 		 int personID = myobj.nextInt();
		 		 
		 		 usecase_.getRoles(personID);
		 		 break;
		 		 
		 	case 2:
		 		 System.out.println("Skriv inn personID på skuespiller:");
		 		 myobj.nextLine();
		 		 
		 		 String personID2 = myobj.nextLine();
		 		 
		 		 usecase_.getFilm(Integer.parseInt(personID2));
		 		 break;
		 		 
		 	
		 	case 3: 
		 		
		 		 usecase_.getCompaniesByGenre();
		 		 break;
		 		 
	 		case 4:
	 			
	 			

				 System.out.println("Skriv inn tittel (String), lengde (int), utgivelsesår (int), lanseringsdato (String), beskrivelse (String),"
							+ "om den er utgitt på video (int), hvilket medium den er laget for skilt med komma uten mellomrom:):");
				 myobj.nextLine();
				 
				 String[] Filmlist = myobj.nextLine().split(",");
				 int filmID = usecase_.InsertMovie(Filmlist[0],Integer.parseInt(Filmlist[1]),Integer.parseInt(Filmlist[2]),Filmlist[3],Filmlist[4],Integer.parseInt(Filmlist[5]),Filmlist[6]);		 
				 
				 ArrayList<String> stillinger = new ArrayList<String>(Arrays.asList("regissør","manusforfatter","skuespiller","komponist","musiker"));
				 
				 
				 for (String stilling : stillinger) {
		 			 while (true) {
		 				System.out.println("Trykk 1 for å legge til en ny "+stilling+", 0 for å gå videre");
		 				
		 				
			 			int valg2 = myobj.nextInt();
			 	
			 			if (valg2 == 0) {
			 				break;
			 			}
			 			
			 			if (stilling == "skuespiller") {
			 				System.out.println("Skriv inn personID og rolle på "+stilling+":");
			 				myobj.nextLine();
			 				String [] person4 = myobj.nextLine().split(",");
			 				usecase_.newActor(Integer.parseInt(person4[0]),stilling,filmID,person4[1]);
			 			}
			 			else {
			 				System.out.println("Skriv inn personID på "+stilling+":");
			 				myobj.nextLine();
			 				int personID3 = myobj.nextInt();
			 				usecase_.newNotActor(personID3,stilling,filmID);
			 			}
		 			 }
				 }
				 
				 
		 		 break;
		 		 
	 		case 5:
	 			System.out.println("Trykk 1 hvis du har bruker eller 2 hvis du ikke har bruker:");
	 			myobj.nextLine();
	 			
	 			if(myobj.nextInt()==2) {
	 				System.out.println("Skriv inn brukernavn:");
		 			myobj.nextLine();
		 			String brukernavn=myobj.nextLine();
	 				usecase_.addBruker(brukernavn);
	 			}
	 			
	 			System.out.println("Skriv anmeldelseforfilm for å legge inn anmeldelse for film eller anmeldelseforepisode for anmeldelse for episode:");
	 			myobj.nextLine();
	 			
	 			String anmeldelseValg=myobj.nextLine();
	 			
 				System.out.println("Skriv inn BrukerID");
	
	 			int brukerID=myobj.nextInt();
	 			System.out.println("Skriv inn FilmID:");

	 			int filmID1=myobj.nextInt();
	 			myobj.nextLine();
	 			System.out.println("Skriv inn kommentar:");
	 
	 			String Kommentar=myobj.nextLine();
	 			System.out.println("Skriv inn Rating som et tall fra 1-10:");
	 			int Rating=myobj.nextInt();
	 			usecase_.newAnmeldelse(anmeldelseValg, brukerID, filmID1, Kommentar, Rating);
	 			break;
	 		}	
		}
		
		 
}
	
}
