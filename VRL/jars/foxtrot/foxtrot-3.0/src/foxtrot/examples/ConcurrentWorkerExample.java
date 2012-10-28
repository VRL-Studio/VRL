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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import foxtrot.ConcurrentWorker;
import foxtrot.Job;

/**
 * An example that shows the correct usage context for {@link ConcurrentWorker}.
 *
 * @version $Revision: 260 $
 */
public class ConcurrentWorkerExample extends JFrame
{
    private JButton button;

    public static void main(String[] args)
    {
        ConcurrentWorkerExample example = new ConcurrentWorkerExample();
        example.setVisible(true);
    }

    public ConcurrentWorkerExample()
    {
        super("ConcurrentWorker Foxtrot Example");
        init();
    }

    private void init()
    {
        button = new JButton("Start long task");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startLongTask();
            }
        });

        Container content = getContentPane();
        content.setLayout(new GridBagLayout());
        content.add(button);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 200);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = getSize();
        int x = (screen.width - size.width) >> 1;
        int y = (screen.height - size.height) >> 1;
        setLocation(x, y);
    }

    private void startLongTask()
    {
        // The dialog that can cancel the long task
        final CancelDialog dialog = new CancelDialog();

        System.out.println("Posting task...");
        ConcurrentWorker.post(new Job()
        {
            public Object run()
            {
                // Shows a modal dialog to prevent the user to click
                // on other parts of the user interface
                Thread workerThread = Thread.currentThread();
                showDialogInEventThread(dialog, workerThread);

                try
                {
                    // Start very long task, for example calling a server
                    Thread.sleep(5000);
                    System.out.println("Task ended");
                }
                catch (InterruptedException x)
                {
                    System.out.println("Task interrupted");
                }

                // Hides the dialog, since the task is either completed
                // or it has been interrupted
                hideDialogInEventThread(dialog);

                return null;
            }
        });
        System.out.println("Task finished");
    }

    private void showDialogInEventThread(final CancelDialog dialog, final Thread workerThread)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                System.out.println("Showing the dialog");
                dialog.display(workerThread);
            }
        });
    }

    private void hideDialogInEventThread(final CancelDialog dialog)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                System.out.println("Hiding the dialog");
                dialog.undisplay();
            }
        });
    }

    private void cancelLongTask(CancelDialog dialog, final Thread workerThread)
    {
        dialog.cancelling();

        ConcurrentWorker.post(new Job()
        {
            public Object run()
            {
                // Simulate a call to the server to cancel the task, signaling
                // that the task posted previously must be interrupted
                sleep(1000);
                if (workerThread != null) workerThread.interrupt();
                return null;
            }
        });
    }

    private void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private class CancelDialog extends JDialog
    {
        private JButton button;
        private volatile Thread workerThread;

        public CancelDialog()
        {
            super(ConcurrentWorkerExample.this, "Cancel Task ?", true);
            init();
        }

        private void init()
        {
            button = new JButton("Cancel");
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    cancelLongTask(CancelDialog.this, workerThread);
                }
            });
            Container content = getContentPane();
            content.setLayout(new GridBagLayout());
            content.add(button);

            setSize(200, 150);
            setLocationRelativeTo(ConcurrentWorkerExample.this);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }

        private void cancelling()
        {
            button.setEnabled(false);
            button.setText("Cancelling...");
        }

        public void display(Thread workerThread)
        {
            this.workerThread = workerThread;
            setVisible(true);
        }

        public void undisplay()
        {
            this.workerThread = null;
            if (isVisible()) dispose();
        }
    }
}
