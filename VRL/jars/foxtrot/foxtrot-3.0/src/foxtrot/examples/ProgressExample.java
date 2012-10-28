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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * An example of how to use progress indication with Foxtrot.
 * The main advantage is that there is no more need to create a separate thread
 * for the progressive operation, but just use the Foxtrot API.
 * And, of course, with Foxtrot the GUI can be interrupted in any moment.
 *
 * @version $Revision: 260 $
 */
public class ProgressExample extends JFrame
{
    private JButton button;
    private JProgressBar bar;
    private boolean running;
    private boolean taskInterrupted;

    public static void main(String[] args)
    {
        ProgressExample example = new ProgressExample();
        example.setVisible(true);
    }

    public ProgressExample()
    {
        super("Foxtrot Example");

        button = new JButton("Run Task !");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (running)
                    onCancelClicked();
                else
                    onRunClicked();
            }
        });

        bar = new JProgressBar();
        bar.setStringPainted(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(0, 0));

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBorder(new EmptyBorder(35, 35, 35, 35));
        c.add(main, BorderLayout.CENTER);

        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.add(bar, BorderLayout.NORTH);
        p.add(button, BorderLayout.SOUTH);

        main.add(p);

        setSize(300, 200);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = getSize();
        int x = (screen.width - size.width) >> 1;
        int y = (screen.height - size.height) >> 1;
        setLocation(x, y);
    }

    private void onRunClicked()
    {
        // We are running
        running = true;

        // We just started, set the task as not interrupted, to
        // clear any eventual previous status
        setTaskInterrupted(false);

        // We will execute a long operation, change the text signaling
        // that the user can interrupt the operation
        button.setText("Cancel");

        // getData() will block until the heavy operation is finished
        // and the AWT-Swing events will be dequeued and processed
        ArrayList list = getData();

        // Restore the button's text
        button.setText("Run Task !");

        // We're not running anymore
        running = false;

        // getData() finished or was interrupted ?
        // If was interrupted we get back a null list
        if (list != null)
        {
            // Task completed successfully, do whatever useful with the list
            // For example, populate a JComboBox
            // The reader will finish this part :)
            DefaultComboBoxModel model = new DefaultComboBoxModel(list.toArray());
        }
    }

    private void onCancelClicked()
    {
        // Here if we want to interrupt the Task

        setTaskInterrupted(true);
    }

    private ArrayList getData()
    {
        return (ArrayList)Worker.post(new Job()
        {
            public Object run()
            {
                ArrayList list = new ArrayList();
                StringBuffer buffer = new StringBuffer();

                // A repetitive operation that updates a progress bar.
                int max = 20;
                for (int i = 1; i <= max; ++i)
                {
                    // Simulate a heavy operation to retrieve data
                    try
                    {
                        Thread.sleep(250);
                    }
                    catch (InterruptedException ignored)
                    {
                    }

                    // Populate the data structure
                    Object data = new Object();
                    list.add(data);

                    // Prepare the progress bar string
                    buffer.setLength(0);
                    buffer.append("Step ").append(i).append(" of ").append(max);

                    if (isTaskInterrupted())
                    {
                        buffer.append(" - Interrupted !");
                        update(i, max, buffer.toString());
                        break;
                    }
                    else
                    {
                        // Update the progress bar
                        update(i, max, buffer.toString());
                    }
                }

                if (isTaskInterrupted())
                {
                    // Task is interrupted, clean the half-populated data structure
                    // and return from the Task
                    list.clear();
                    return null;
                }
                else
                {
                    return list;
                }
            }
        });
    }

    private void update(final int index, final int max, final String string)
    {
        // This method is called by the Foxtrot Worker thread, but I want to
        // update the GUI, so I use SwingUtilities.invokeLater, as the Task
        // is not finished yet.

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                bar.setMaximum(max);
                bar.setValue(index);
                bar.setString(string);
            }
        });
    }

    private synchronized boolean isTaskInterrupted()
    {
        // Called from the Foxtrot Worker Thread.
        // Must be synchronized, since the variable taskInterrupted is accessed from 2 threads.
        // While it is easier just to change the variable value without synchronizing, it is possible
        // that the Foxtrot worker thread doesn't see the change (it may cache the value of the variable
        // in a registry).
        return taskInterrupted;
    }

    private synchronized void setTaskInterrupted(boolean value)
    {
        // Called from the AWT Event Dispatch Thread.
        // See comments above on why it must be synchronized.
        taskInterrupted = value;
    }
}
