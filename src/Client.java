import java.io.*;
import java.net.Socket;

/**
 * La classe Client represente le client TCP du serveur de chat. Il implemente IObserver pour respecter le patron
 * observateur. En effet, le client observe le serveur qui le notifie quand un message est arrivé par un autre
 * client et doit être envoyé au Client connectés.
 */
public class Client implements Runnable {

    private Socket socket;
    private String nom;
    private BufferedReader input;
    private BufferedWriter output;
    private ServeurChat serveur;

    /**
     *  Constructeur du client
     * @param socket Socket du client
     * @param serveur Serveur de chat
     */
    public Client(Socket socket, ServeurChat serveur) {
        this.socket = socket;
        this.serveur = serveur;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Methode run executée par lors de l'initialisation du Thread du client.
     */
    @Override
    public void run() {
        this.demanderNom();
        //On enregistre le client auprès du serveur
        serveur.enregisterClient(this);
        this.communiquerAvecClient();
        serveur.deconnecterClient(this);
    }

    /**
     * Boucle permettant de communiquer avec le client
     */
    private void communiquerAvecClient() {
        String message;
        do {
            message = ecouter();
            //On envoie le message a tous les clients connectés, sauf le client actuel
            if (!message.equals("bye")) {
                serveur.getBoiteAuxLettres().put("[" + this.nom + "] " + message + "\n");
                serveur.getBoiteEnvoie().envoyerMessage(this);
            }
        } while (!message.equals("bye")); //On quitte quand la reponse du client est bye
    }

    /**
     * Permet de connaitre le nom du client pour qu'il soit identifie
     */
    private void demanderNom() {
        envoyer("Quel est votre nom ?\n");
        this.nom = ecouter();
    }


    /**
     * Permet d'envoyer un message au client
     * @param msg
     */
    public void envoyer(String msg) {
        try {
            output.write(msg);
            output.flush();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'attendre un message du client, methode bloquante
     * @return
     */
    public String ecouter() {
        try {
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Getters et setters
     * @return
     */

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }

    public BufferedWriter getOutput() {
        return output;
    }

    public void setOutput(BufferedWriter output) {
        this.output = output;
    }
}
