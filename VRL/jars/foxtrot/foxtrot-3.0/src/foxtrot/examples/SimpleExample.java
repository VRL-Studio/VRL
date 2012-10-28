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

import javax.swing.JButton;
import javax.swing.JFrame;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * A very simple example that shows Swing limits. <br />
 * A button executes a time-consuming task, and when executing this task the same button is used to
 * interrupt the task. <br />
 * This is impossible to do with plain Swing, but easy with Foxtrot.
 * <p/>
 * For a comparison, look at the freezeSleep method and to the workerSleep method, and try to run the
 * example one time with one method, and one time with the other.
 *
 * @version $Revision: 260 $
 */
public class SimpleExample extends JFrame
{
    private boolean sleeping;
    private JButton button;

    public static void main(String[] args)
    {
        SimpleExample example = new SimpleExample();
        example.setVisible(true);
    }

    public SimpleExample()
    {
        super("Foxtrot Example");

        button = new JButton("Take a nap !");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (sleeping)
                    wakeUp();
                else
                    sleep();
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

    private void sleep()
    {
        button.setText("Wake me up!");
        workerSleep();
//		freezeSleep();
        button.setText("Take a nap!");
    }

    private void workerSleep()
    {
        try
        {
            Worker.post(new Task()
            {
                public Object run() throws InterruptedException
                {
                    synchronized (SimpleExample.this)
                    {
                        sleeping = true;
                        SimpleExample.this.wait();
                        sleeping = false;
                    }
                    return null;
                }
            });
        }
        catch (InterruptedException x)
        {
            x.printStackTrace();
        }
        catch (Exception x)
        {
            // Never thrown
        }
    }

    private void freezeSleep()
    {
        try
        {
            synchronized (this)
            {
                sleeping = true;
                wait();
                sleeping = false;
            }
        }
        catch (InterruptedException x)
        {
            x.printStackTrace();
        }
    }

    private void wakeUp()
    {
        synchronized (this)
        {
            notifyAll();
        }
    }
}
