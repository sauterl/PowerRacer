package shared.protocol;

/**
 * @author marco
 * @author benzumbrunn
 * 
 */
public enum Protocol {

	// 1: Client initialized packets

	// 1.1: Packets client to server

	/**
	 * Client/Server sends Game CoLlidable Removal Request.
	 */
	GCLRR {
	},
	/**
	 * Client/Server sends Game CoLlidable Creation Request.
	 */
	GCLCR {
	},
	/**
	 * Client sends Game CoLlidable Disabled Information.
	 */
	GCLDI {
	},
	/**
	 * Client sends a login request with a username.
	 */
	LGINR {
	},
	/**
	 * Client sends a logout request to server.
	 */
	LGOUR {
	},
	/**
	 * Client sends a rename request with new name.
	 */
	NAMCR {
	},
	/**
	 * Client sends a chat request to all connected clients on the server.
	 */
	CALLR {
	},
	/**
	 * Client sends a chat lobby to all clients in the same lobby.
	 */
	CLOBR {
	},
	/**
	 * Client sends a whisper request to another client on the server.
	 */
	CWHIR {
	},
	/**
	 * Who is online request.
	 */
	WHOOR {
	},
	/**
	 * Lobby chat request.
	 */
	LOBCR {
	},
	/**
	 * Lobby join request.
	 */
	LOBJR {
	},
	/**
	 * Lobby leave request.
	 */
	LOBLR {
	},
	/**
	 * Heartbeat request by client.
	 */
	HBTCR {
	},
	/**
	 * Lobby ready request.
	 */
	LOBRR {
	},
	/**
	 * Lobby unready request.
	 */
	LOBUR {
	},
	/**
	 * Game finished information cts
	 */
	GFINI {
	},
	/**
	 * Game creation request "GCRER:carIndex:2" cts
	 */
	GCRER {
	},
	/**
	 * Game pause request.
	 */
	GPAUR {
	},
	/**
	 * Game continue request.
	 */
	GCONR {
	},

	/**
	 * Previous games (history) request.
	 */
	PREGR {
	},
	/**
	 * Current games request.
	 */
	CURGR {
	},
	/**
	 * Collidable removed information.
	 */
	GCLRI {
	},
	// 1.2: Packets server to client

	/**
	 * Server sends Game CoLlidable Creation Approval with old id and new id.
	 */
	GCLCA {
	},
	/**
	 * Server accepts the login request and sends the client his username or
	 * numbered username.
	 */
	LGINA {
	},
	/**
	 * Server sends client a login denial packet if server is full.
	 */
	LGIND {
	},
	/**
	 * Server answers with a logout approval.
	 */
	LGOUA {
	},
	/**
	 * Server accepts the renaming.
	 */
	NAMCA {
	},
	/**
	 * Server denies the renaming.
	 */
	NAMCD {
	},
	/**
	 * Server accepts the message (whisper).
	 */
	CWHIA {
	},
	/**
	 * Server denies the message (whisper), no client with this name.
	 */
	CWHID {
	},
	/**
	 * Who is online information.
	 */
	WHOOI {
	},
	/**
	 * Lobby create approval.
	 */
	LOBCA {
	},
	/**
	 * Lobby create denial.
	 */
	LOBCD {
	},
	/**
	 * Lobby join approval.
	 */
	LOBJA {
	},
	/**
	 * Lobby join denial.
	 */
	LOBJD {
	},
	/**
	 * Lobby leave approval.
	 */
	LOBLA {
	},
	/**
	 * Lobby leave denial.
	 */
	LOBLD {
	},
	/**
	 * Lobby ready approval.
	 */
	LOBRA {
	},
	/**
	 * Lobby unready approval.
	 */
	LOBUA {
	},
	/**
	 * Server sends a heartbeat back to the client.
	 */
	HBTCA {
	},
	/**
	 * Server Broadcast to all Players stc/cts
	 */
	SBROI {
	},
	/**
	 * Game creation approval. stc
	 */
	GCREA {
	},
	/**
	 * Game input information. stc / cts
	 */
	GINPI {
	},
	/**
	 * Game highscore information. stc
	 */
	GHISI {
	},
	/**
	 * Game countdown information. GCODI:countdown:3, GCODI:countdown:2,
	 * GCODI:countdown:1, GCODI:countdown:0, stc
	 */
	GCODI {
	},
	/**
	 * Game completed information.
	 * GCOMI:rang:Beni:rang:Marco:rang:Simeon:rang:Florian stc gfini return key
	 */
	GCOMI {
	},
	/**
	 * Game pause approval.
	 */
	GPAUA {
	},
	/**
	 * Game pause denial.
	 */
	GPAUD {
	},
	/**
	 * Game continue approval.
	 */
	GCONA {
	},
	/**
	 * Game continue denial.
	 */
	GCOND {
	},

	// 1.3: Server to all

	/**
	 * Login information.
	 */
	LGINI {
	},
	/**
	 * Logout information.
	 */
	LGOUI {
	},
	/**
	 * Namechange information.
	 */
	NAMCI {
	},
	/**
	 * Chat all message.
	 */
	CALLM {
	},
	/**
	 * Chat lobby message.
	 */
	CLOBM {
	},
	/**
	 * Chat whisper message.
	 */
	CWHIM {
	},
	/**
	 * Lobby create information.
	 */
	LOBCI {
	},
	/**
	 * Lobby join information.
	 */
	LOBJI {
	},
	/**
	 * Lobby leave information.
	 */
	LOBLI {
	},
	/**
	 * Lobby open information.
	 */
	LOBOI {
	},
	/**
	 * Lobby destruction information.
	 */
	LOBDI {

	},
	/**
	 * Game pause information.
	 */
	GPAUI {
	},
	/**
	 * Game continue information.
	 */
	GCONI {
	},

	/**
	 * Previous games (history) information. Contains each line of the servers
	 * csv document seperated by a ":".
	 */
	PREGI {
	},
	/**
	 * Previous games (history) denial because there was an IOexception(file not
	 * found mostly)..
	 */
	PREGD {
	},
	/**
	 * Current games information.
	 */
	CURGI {
	},
	/**
	 * Current game denial because there are no current games.
	 */
	CURGD {
	},

	/**
	 * Server Kick packet
	 */
	SKICK {
	},

	/**
	 * PowerUp-effect information
	 */
	GCEFI {
	},
	/*
	 * **************************************
	 * SERVERSIDE INITIALISED COMMUNICATION
	 * **************************************
	 */
	// 2.1: Server to client

	/**
	 * Heartbeat request to client.
	 */
	HBTSR {
	},

	/**
	 * Game new random map generated.
	 */
	GNRMG {
	},

	// 2.2: Client to server

	/**
	 * Heartbeat approval to server.
	 */
	HBTSA {
	};
}
