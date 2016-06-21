# BitTorrent-Client-Example
BitTorrent Client Example implemented in 2013.

## Project definition
The purpose of this project is designing and developing a BitTorrent client. The client must provide the following functions:
* Open and process _.torrent_ files.
* Connect to a tracker and interact with it using and HTTP connection.
* Connect to different peers (seeders and leechers) over TCP connections.

The basic client should work as follows:
1. The program must the name of a _torrent_ file as parameter.
2. The _torrent_ file will be decoded in order to check the information of the content to download.
3. Keep needed space in the disk for saving the downloaded content.
4. Start a TCP connection with the Tracker (<a href="http://www.utorrent.com/intl/es/">µTorrent</a> can be used as local Tracker for testing) and send an HTTP GET request.
5. Process the response of the Tracker and extract the IP addresses and ports of the Peers.
6. Open TCP connections with the Peers and perform some simultaneous downloads.
7. Ask for pieces to another Peers and download them.
8. Check the SHA-1 hash of each downloaded piece and store it.
9. Notify the Peers every new correctly downloaded piece.
10. Receive and manage download requests from another Peers.
11. Repeat steps 7-10 until the download is finished.
12. Eventually contact the Tracker for the purpose of updating the state.
13. Ensure that the client shares at least a 10% of the size of the stored file before finishing the download.
14. When the download finishes, contact again the Tracker.

The download can be stopped. The client has to store the information about the state of the download, in order to resume it when the client will be launched again. Only correctly downloaded and checked pieces will be stored.

__Note:__ the BitTorrent protocol and the practice of the Stanford University might be used as reference:
* <a href="http://bnrg.cs.berkeley.edu/~adj/cs16x/">http://bnrg.cs.berkeley.edu/~adj/cs16x/</a> (Project #3).
* <a href="http://jonas.nitro.dk/bittorrent/bittorrent-rfc.html">http://jonas.nitro.dk/bittorrent/bittorrent-rfc.html</a>.
* <a href="https://wiki.theory.org/BitTorrentSpecification">https://wiki.theory.org/BitTorrentSpecification</a>.

## Design
### Thread and socket architecture
On the one hand, an HTTP/UDP socket will be used for the communication with the tracker. Two threads will be used: one for listening and another one for sending messages.

In the other hand, a TCP socket will be used for each peer. Every socket will have two threads: one for listening and another one for sending messages.

The size of the block is specified by the tracker. The blocks will be divided in 16kB pieces. The peers and the client will send one to each other these pieces in order to complete blocks.

### Info about the peers
Some information about the peers given by the tracker has to be stored:
* Alive: determines if the peer is alive or not. After two minutes since the last _keepAlive_ message, the peer will be set to _not alive_ and the messages incoming from it will be ignored. If more than the half of the peers are not alive, the peers which are not alive will be removed and the client will ask the tracker for more.
* Chocked: specifies if the peer is chocked. Any message received from chocked peers will be ignored.
* User chocked: specifies if the peer has chocked the client. If the client is chocked, no messages will be sent to this peer until it unchockes the client.
* Interested: specifies if the client is interested in the peer.
* Interested peer: specifies if the peer is interested in the client.
* Parts owned: the parts the peer has downloaded. The client will ask the peer for parts which it owns and the client is interested in.

This information will be stored in a `Peer` object. It will have other parameters, like the IP, port, socket...

### 10% shared warranty
It the user has not shared the 10% of the file size with other peers, the last remaining block will not be downloaded until the shared percentage will be a 10% at least.

For every file the user is downloading, the program will store the total amount of data shared with other peers, in order to check the percentage.

### Necessary information
The client will need to manage some information: downloading files list, the progress of the download of every file which is being downloaded, a list of peers, IPs...

### File
A file will be created in the _downloads_ folder. The size of this file will match the one of the downloading file. As the blocks are downloaded, the hash will be checked. If it is correct, the block will be written into the file. Otherwise, the block will be discarded and downloaded again.