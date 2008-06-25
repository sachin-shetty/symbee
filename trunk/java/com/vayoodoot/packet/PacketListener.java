package com.vayoodoot.packet;

import com.vayoodoot.util.VDRunnable;
import com.vayoodoot.util.VDThreadRunner;
import com.vayoodoot.util.Queue;
import com.vayoodoot.exception.VDException;

import java.net.DatagramPacket;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class is used to listen to the packets that are coming in,
 * it reads the packet and then places it on the queue,
 * it is very important that the this thread processes the incoming
 * packets as quickly as possible and go back to listening to socket to
 * avoid any packet loss.
 */
public class PacketListener implements VDRunnable {


    private static Logger logger = Logger.getLogger(PacketListener.class);

    /**
     * Name of the listener, used for thread naming
     */
    private String name;
    private VDDatagramSocket socket;

    private Queue packetQueue = new Queue();

    private VDThreadRunner thread;

    public PacketListener(String name, VDDatagramSocket socket) {
        this.name = name;
        this.socket = socket;
    }

    public void startListening() throws PacketException {

        thread = new VDThreadRunner(this, "PacketListsner:" + name);
        try {
            thread.startRunning();
        } catch(VDException vde) {
            throw new PacketException(name + ":Error in starting thread for packet listener: " + vde, vde);
        }

    }

    public void keepDoing() throws VDException {

        try {
            DatagramPacket packet = new DatagramPacket(new byte[3000], 3000);
            logger.debug("Waiting for datagram Packet");
            socket.receive(packet);
            logger.debug("Packet Obtained");
            // Put the packet in the queue
            synchronized(packetQueue) {
                logger.debug("Received a packet of length:" + packet.getLength());
                packetQueue.add(packet);
                packetQueue.notifyAll();
            }

        } catch(IOException ie) {
            if (!socket.isBound()) {
                throw new VDException(name + ":Error when listeneing to datagram socket: " + ie, ie);

            }
            else {
                logger.info("Socket intentionally closed");
                thread.stop();
            }
        }


    }

    // Get the next packet, it blocks until the packet is available
    public DatagramPacket getNextPacket() {

        synchronized(packetQueue) {
            DatagramPacket packet = null;
            logger.debug("Trying to get a packet ");
            while((packet = (DatagramPacket)packetQueue.getNextObject()) == null) {
                logger.debug("Waiting for packets: ");
                try {
                    packetQueue.wait();
                } catch (InterruptedException ie) {
                    logger.fatal(name + ": Caught Interrupted exception when waiting for packer queue: "
                            + ie, ie);
                    return null;
                }
            }
            logger.debug("Pickeing up packet: " + packet.getLength());
            return packet;

        }

    }

    public void stop() {

        thread.stop();
        thread.interrupt();

    }


}
