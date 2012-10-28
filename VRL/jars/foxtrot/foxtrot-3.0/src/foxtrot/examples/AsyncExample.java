/**
 * Copyright (c) 2002-2008, Simone Bordet
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package foxtrot.examples;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import foxtrot.AsyncTask;
import foxtrot.AsyncWorker;

/**
 * A simple example that shows how to use the Foxtrot API in case of use
 * of the asynchronous model, similar to the SwingWorker.
 *
 * @version $Revision: 259 $
 */
public class AsyncExample extends JFrame
{
    public static void main(String[] args)
    {
        AsyncExample example = new AsyncExample();
        example.setVisible(true);
    }

    private JLabel tasksSending;
    private JLabel tasksSent;
    private JButton button;
    private boolean sent;

    public AsyncExample()
    {
        super("Async Foxtrot Example");
        init();
    }

    private void init()
    {
        tasksSending = new JLabel();
        tasksSent = new JLabel();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 0));
        panel.add(tasksSending);
        panel.add(tasksSent);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(panel, BorderLayout.NORTH);

        button = new JButton();
        c.add(button, BorderLayout.SOUTH);
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (sent)
                    reset();
                else
                    send();
            }
        });

        reset();

        setSize(300, 200);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = getSize();
        int x = (screen.width - size.width) >> 1;
        int y = (screen.height - size.height) >> 1;
        setLocation(x, y);
    }

    public void send()
    {
        tasksSending.setText("Task submitted, waiting for result...");
        button.setText("Sending...");
        AsyncWorker.post(new AsyncTask()
        {
            public Object run() throws Exception
            {
                // Send the data to a system that is asynchronous
                // Assume it will take time to send
                Thread.sleep(2000);
                return System.getProperty("user.dir");
            }

            public void success(Object result)
            {
                // The result is what we returned from run() above
                String data = (String)result;
                tasksSent.setText("Result: " + data);
                button.setText("Reset");
                sent = true;
            }

            public void failure(Throwable x)
            {
                // Show the problem to the user
                x.printStackTrace();
            }
        });
    }

    public void reset()
    {
        sent = false;
        tasksSending.setText("No tasks submitted");
        tasksSent.setText("Result:");
        button.setText("Click Me !");
    }
}
