/**
 * Copyright (c) 2002-2008, Simone Bordet
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package foxtrot.examples;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * An example of how to create a Task that is interruptible. <br />
 * This is not provided by the Foxtrot API, because it is too much application dependent: what are
 * the correct actions to take when a Task is interrupted ? <br />
 * This implies that the Task must be collaborative, and check once in a while if it is interrupted
 * by another thread.
 *
 * @version $Revision: 260 $
 */
public class InterruptExample extends JFrame
{
    private JButton button;
    private boolean running;
    private boolean taskInterrupted;

    public static void main(String[] args)
    {
        InterruptExample example = new InterruptExample();
        example.setVisible(true);
    }

    public InterruptExample()
    {
        super("Foxtrot Example");

        final String label = "Run Task !";
        button = new JButton(label);
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onButtonClick(label);
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(button);

        setSize(300, 200);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = getSize();
        int x = (screen.width - size.width) >> 1;
        int y = (screen.height - size.height) >> 1;
        setLocation(x, y);
    }

    private void onButtonClick(final String label)
    {
        if (!running)
        {
            running = true;

            // We will execute a long operation, change the text signaling
            // that the user can interrupt the operation
            button.setText("Interrupt");

            try
            {
                // getData() will block until the heavy operation is finished
                ArrayList list = getData();

                // getData() finished or was interrupted ?
                // If was interrupted we get back a null list
                if (list == null)
                {
                    // Task was interrupted, return quietly, we already cleaned up
                    return;
                }
                else
                {
                    // Task completed successfully, do whatever useful with the list
                    // For example, populate a JComboBox
                    javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel(list.toArray());
                    // The reader will finish this part :)
                }
            }
            catch (Exception x)
            {
                // Problems during getData(), log this exception and return
                x.printStackTrace();
            }
            finally
            {
                // Restore anyway the button's text
                button.setText(label);

                // Restore anyway the interrupt status for another call
                setTaskInterrupted(false);

                // We're not running anymore
                running = false;
            }
        }
        else
        {
            // Here if we want to interrupt the Task

            // Restore the button text to the previous value
            button.setText(label);

            // Interrupt the task
            setTaskInterrupted(true);
        }
    }

    private ArrayList getData() throws Exception
    {
        return (ArrayList)Worker.post(new Task()
        {
            public Object run() throws Exception
            {
                System.out.println("Task started...");

                ArrayList list = new ArrayList();

                // A repetitive operation that checks if it is interrupted.
                // The heavy task must collaborate !
                for (int i = 0; i < 100; ++i)
                {
                    System.out.println("Heavy Operation number " + (i + 1));

                    // Simulate a heavy operation to retrieve data
                    Thread.sleep(250);

                    // Populate the data structure
                    Object data = new Object();
                    list.add(data);

                    System.out.println("Checking if task is interrupted...");
                    if (isTaskInterrupted())
                    {
                        System.out.println("Task interrupted !");
                        break;
                    }
                    System.out.println("Task not interrupted, going on");
                }

                if (isTaskInterrupted())
                {
                    // Task is interrupted, clean the half-populated data structure
                    // and return from the Task
                    list.clear();
                    return null;
                }

                return list;
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
