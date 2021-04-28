package org.vincentyeh.IMG2PDF.commandline.action.exception;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.MissingOptionException;
import org.vincentyeh.IMG2PDF.commandline.Configuration;

public class CustomMissingOptionException extends MissingOptionException {

    /** The list of missing options and groups */
    private List missingOptions;

    public CustomMissingOptionException(String message)
    {
        super(message);
    }

    
    /**
     * Constructs a new <code>MissingSelectedException</code> with the
     * specified list of missing options.
     *
     * @param missingOptions the list of missing options and groups
     * @since 1.2
     */
    public CustomMissingOptionException(List missingOptions)
    {
        this(createMessage(missingOptions));
        this.missingOptions = missingOptions;
    }

    /**
     * Build the exception message from the specified list of options.
     *
     * @param missingOptions the list of missing options and groups
     * @since 1.2
     */
    private static String createMessage(List<?> missingOptions)
    {
        StringBuilder buf = new StringBuilder(Configuration.getResString("err_missing_option"));
        buf.append(": ");

        Iterator<?> it = missingOptions.iterator();
        while (it.hasNext())
        {
            buf.append(it.next());
            if (it.hasNext())
            {
                buf.append(", ");
            }
        }

        return buf.toString();
    }
}
