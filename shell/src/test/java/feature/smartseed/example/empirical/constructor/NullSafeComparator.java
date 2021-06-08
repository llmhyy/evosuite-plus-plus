package feature.smartseed.example.empirical.constructor;

/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


import java.util.Comparator;

/**
 * Comparator Decorator that adds support for null-Value comparison.
 * null may be defined to have a value either greater or less than any non-null value.
 * Created: 11.04.2005 08:34:02
 */
public class NullSafeComparator<E> implements Comparator<E> {

    public static final int NULL_IS_LESSER   = -1;
    public static final int NULL_IS_GREATER =  1;

    private Comparator<? super E> realComparator;

    /** The value returned if null is compared to something.
     */
    private int nullComparation;

    // constructors ----------------------------------------------------------------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public NullSafeComparator() {
        this(new ComparableComparator());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public NullSafeComparator(int nullComparation) {
        this(new ComparableComparator(), nullComparation);
    }

    public NullSafeComparator(Comparator<? super E> realComparator) {
        this(realComparator, -1);
    }

    public NullSafeComparator(Comparator<? super E> realComparator, int nullComparation) {
        this.realComparator = realComparator;
        this.nullComparation = nullComparation;
    }

    // interface -------------------------------------------------------------------------------------------------------

    public int compare(E o1, E o2) {
        if (o1 == o2)
            return 0;
        if (o1 == null)
            return (o2 == null ? 0 : nullComparation);
        else if (o2 == null)
            return -nullComparation;
        else
            return realComparator.compare(o1, o2);
    }

    public static <T extends Comparable<T>> int compare(T o1, T o2, int nullComparation) {
        if (o1 == o2)
            return 0;
        if (o1 == null)
            return (o2 == null ? 0 : nullComparation);
        else if (o2 == null)
            return -nullComparation;
        else
            return o1.compareTo(o2);
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == null)
            return (o2 == null);
        else
            return o1.equals(o2);
    }

	public static int hashCode(Object o) {
		return (o != null ? o.hashCode() : 0);
	}
}

