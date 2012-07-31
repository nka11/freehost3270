/*
 * FreeHost3270 a suite of terminal 3270 access utilities.
 * Copyright (C) 1998, 2001  Art Gillespie
 * Copyright (2) 2005 the http://FreeHost3270.Sourceforge.net
 *                        Project Contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package net.sf.freehost3270.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;

import net.sf.freehost3270.client.Host;
import net.sf.freehost3270.client.IsProtectedException;
import net.sf.freehost3270.client.RW3270;
import net.sf.freehost3270.gui.JTerminalScreen;


/**
 * An applet that launches the Freehost terminal emulator GUI client.
 */
public class FreeHostApplet extends JApplet implements ComponentListener {
    private static final Logger log = Logger.getLogger(FreeHostApplet.class.getName());
    private JTerminalScreen scr;
    private Map hosts = new Hashtable();
    private String proxyHost = null;
    private int proxyPort;

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        if (e.getSource() == scr) {
            validate();
        }
    }

    public void componentShown(ComponentEvent e) {
    }

    public void connect() {
        log.info("connecting");

        if (scr != null) {
            Host dest = (Host) (hosts.values().toArray())[0];

            // TODO: make destination host selectable
            try {
				scr.connect(dest.getHostName(),
				    dest.getPort());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        log.info("connected");
    }

    public void destroy() {
        disconnect();
    }

    public void disconnect() {
        if (scr != null) {
            scr.disconnect();
        }
    }

    public void init() {
        String proxyPortStr = getParameter("proxy-port");

        proxyHost = getDocumentBase().getHost();

        if (proxyPortStr == null) {
            proxyPort = 6728;
            log.warning("proxy port is not specified, using default " +
                proxyPort);
        } else {
            proxyPort = Integer.parseInt(proxyPortStr, 10);
        }

        String available = getParameter("avail-hosts");

        StringTokenizer st = new StringTokenizer(available, "|");

        while (st.hasMoreElements()) {
            String hostName = st.nextToken();
            int hostPort = Integer.parseInt(st.nextToken());
            String friendlyName = st.nextToken();
            hosts.put(friendlyName, new Host(hostName, hostPort, friendlyName));
        }

        buildGui();
    }

    public void redrawScreen() {
        if (scr != null) {
            resize(scr.getSize());
            scr.renderScreen();
            scr.repaint();
        }
    }

    private void buildGui() {
        Container contentPane = getContentPane();
        scr = new JTerminalScreen();
        scr.setAlignmentX(CENTER_ALIGNMENT);
        contentPane.setBackground(Color.BLACK);
        this.addKeyListener(scr);
        scr.setFont(new Font("Monospaced", Font.PLAIN, 16));
        contentPane.add(Box.createHorizontalStrut(this.getWidth()/2-scr.getWidth()/2),BorderLayout.WEST);
        contentPane.add(scr,BorderLayout.CENTER);
        connect();
        
		scr.requestFocusInWindow();
    }
}
