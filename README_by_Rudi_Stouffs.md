###The Concept of Sorts
by Rudi Stouffs, Ph.D.
17 July 1996

####Concepts
Information types define sorts, e.g., points, labels, lines.
The basic element of a sort is an individual, e.g.,
a point is an individual of the sort of points.
A form is a collection of one or more individuals
of the same simple sort, e.g., a set of points.
Forms can be operated upon algebraically, that is,
one can take the sum, difference or product of two forms of the same sort.
Different algebraic behaviors specify different categories of sorts.
Points, labels and lines are all discrete sorts.
Particular to any discrete sort is that any two of its individuals
can always be said to be either equal (or coincident) or disjoint.
The discrete category is a multiply-associated category.
Multiply-associated sorts allow a form to contain many individuals.
In contrast, singly-associated sorts, e.g., (line) weights, colors,
allow a form to contain only a single individual.
An example of a singly-associated category is the ordinal category.
Another example of a multiply-associated category is
the interval category to which the sort of line segments belongs.

An individual can be assigned an attribute, that is a form
of another (or the same) simple sort.
The resulting sort is a simple sort that is the result of a composition
of two simple sorts, the first corresponding the individual,
the second corresponding the attribute form, under cartesian product ('*').
Thus, every simple sort is defined by its characteristic individual,
that is an abstraction of the sort's individuals, its category,
and possibly an attribute sort, also called weight.
This weight can itself have an attribute sort, thus allowing
for a simple sort to be a composition of multiple simple sorts,
all under cartesian product.
Simple sorts can also be composed under sum ('+'),
the resulting sort is called a composite sort.
The corresponding composition of forms is called a metaform.
If the cartesian product specifies a conjunction of simple sorts,
the sum specifies a disjunction of simple sorts,
each of these sorts may or may not supply a form to the composite sort's
metaform.

##Functionality of the kernel

###A. Sorts and Metaforms: The basic approach.

####1. How to create a (composite) sort.

A composite sort is created according to a given definition,
specified as a parsable string.

Java:	String definition = "..";
	Sort s1 = new Sort(definition);
	CompositeSort s2 = new CompositeSort(definition);
	CompositeSort s3 = new Sort(definition);
	Sort s4 = (Sort) new CompositeSort(definition);

C:	StringBuffer definition = string_buffer();
	buffer(definition, "..");
	Sort s1 = sort(definition);
	CompositeSort s2 = composite_sort(definition);
	CompositeSort s3 = sort(definition);
	Sort s4 = composite_sort(definition);
	recycle_string_buffer(definition);

The terms Sort and CompositeSort can be used interchangeably,
except that, in Java, a cast is needed to convert a CompositeSort into a Sort.

The definition of a (composite) sort is an expression, defined as follows:

	expression := identifier '=' expression | term
	term := subterm | subterm '+' term
	subterm := factor | factor '*' subterm
	factor := '(' expression ')' | '[' char_ind ']' | identifier
	char_ind := identifier

The operators '+' and '*' denote the operations of sum and cartesian product
on sorts, respectively. Here, the operands may themselves be expressions,
thus defining composite sorts. The parser reformats this into a single
sum of simple sorts.
The operator '=' denotes the assignment operator, where the identifier
specifies the name to which the definition is assigned.
The char_ind identifier specifies the characteristic individual of this simple
sort. Each characteristic individual uniquely specifies its category.
In Java, the char_ind identifier takes the appropriate class name.
In C, the category names are contained in the array characters[]
(see "characteristic.c").

Currently, the following characteristic individuals are supported:
Point, Line, LineSegment, Label, Real, Fractional.

I envision to extend the specification of an expression factor as follows:

	factor := product '(' identifier ',' identifier ')' |
		difference '(' identifier ',' identifier ')' |
		symdifference '(' identifier ',' identifier ')' |
		'(' expression ')' | '[' char_ind ']' | identifier

####2. How to create a metaform

Upon creating a metaform, one must specify the corresponding composite sort,
either by the sort object or by its name.

Java:	String name = "..";
	MetaForm m1 = new MetaForm(s1);
	MetaForm m2 = new MetaForm(name);
	MetaForm m3 = s1.newMetaForm();
	MetaForm m4 = Sort.newMetaForm(name);
	MetaForm m5 = CompositeSort.newMetaForm(name);

C:	MetaForm m1 = meta_form(s1);
/* to be provided:
	String name = "..";
	MetaForm m2 = meta_form(to_sort(name)); */

Name denotes the name of a sort that has been assigned a definition before.

####3. How to create an individual

Currently, the following characteristic individuals are supported:
Point, Line, LineSegment, Label, Real, Fractional.

Java:	long x = .., y = .., z = .., w = ..;
	float xf = .., yf = .., zf = ..;
	Coord xc = new Coord(x), yc = new Coord(y), zc = new Coord(z), wc = new Coord(w);
	Vector u = new Vector(xc, yc, zc), v = new Vector(xc, yc, zc, wc);
	Individual i1 = new Point(x, y, z);
	Individual i2 = new Point(x, y, z, w);
	Individual i3 = new Point(xf, yf, zf);
	Individual i4 = new Point(u);
	Individual i5 = new Point(v);
	Individual j1 = new Line(u, v);
	Individual j2 = new Line(i1, i2);
	Individual k1 = new LineSegment(u, v);
	Individual k2 = new LineSegment(i1, i2);
	String label = "..";
	Individual l1 = new Label(label);
	double d = ..;
	Individual ir = new Real(d);
	Individual if = new Fractional(d);

C:	long x = .., y = .., z = .., w = ..;
	Coord xc = coord(x), yc = coord(y), zc = coord(z), wc = coord(w);
	Rational wr = rational(ONE, wc); c_free(wc);
	Vector u = vector(xc, yc, zc, r_ONE), v = vector(xc, yc, zc, wr);
	Individual i1 = point(u);
	Individual i2 = point(v);
	Individual j1 = line(u, v);
	Individual j2 = line(_point(i1)->position, _point(i2)->position);
	Individual k1 = line_segment(u, v);
	Individual k2 = line_segment(_point(i1)->position, _point(i2)->position);
	StringBuffer label = string_buffer();
	buffer(label, "..");
	Individual l1 = label(label);
	buffer(label, "..");
	Individual l2 = label(label);
	double d = ..;
	Individual ir = real(d);
	Individual if = fractional(d);
	recycle_string_buffer(label);
	v_free(u); v_free(v);
	c_free(xc); c_free(yc); c_free(zc); r_free(wr); 

####4. How to add an individual to a metaform

The function checks if the individual's simple sort is a component
of the metaform's composite sort.
The resulting metaform is always maximal.

Java:	m1.add(i1);

C:	add(m1, i1);

####5. How to compare (composite) sorts

Sorts can be compared for equality or to see if one is a part of the other.

Java:	if (s1.equals(s2)) ..
	else if (s1.partOf(s2)) ..

C:	if (equals(s1, s2)) ..
	else if (part_of(s1, s2)) ..

Two composite sort are equal if they have the exact same simple sort components.
A composite sort is a part of another composite sort if the components
of the first sort are also all components of the second sort.

####6. How to compare metaforms

Metaforms can be compared for equality or to see if one is a part of the other.

Java:	 if (m1.equals(m2)) ..
	else if (m1.partOf(m2)) ..

C:	if (equals(m1, m2)) ..
	else if (part_of(m1, m2)) ..

Generally, only metaforms of the same (composite) sort will be compared.
However, sometimes it may be interesting to compare metaforms that
do not belong to the same sort, for example, if one sort is a part of another.
The above functions allow metaforms to be compared only if
one sort is a part of the other (in whatever order) or these are equal.

####7. How to perform algebraic operations on metaforms

The following algebraic operations are available on metaforms:
sum, difference, product and symmetric difference are binary operations
each yielding a single metaform as result;
partition is a binary operation that yields three metaforms as result,
the respective differences as well as the product of both metaforms.

Java:	m1.sum(m2);
	m1. difference(m3);
	m1.product(m4);
	m1.symdifference(m5);
	m1.partition(m6, m2);

C:	sum(m1, m2);
	difference(m1, m3);
	product(m1, m4);
	sym_difference(m1, m5);
	partition(m1, m6, m2);

The operations of sum, product and symmetric difference are (conceptually)
commutative operations. The corresponding functions yield their result
in the first operand argument, while the second operand argument is purged.
The difference function also yields its result in the first operand argument,
but leaves the second operand argument unaltered.
The partition function places the respective differences in both operand
arguments and the product in the additional third argument.
This third argument metaform is first purged if it was not empty.

####8. How to duplicate a metaform.

There are two forms of duplicate: The first assigns a complete duplicate
to a metaform variable; the second duplicates the metaform's forms
to a second (given) metaform. While, in the second case, the two metaforms
do not have to be of the same sort, it is required that the sort of
the original sort is at least a part of the sort of the receiving metaform.

Java:	MetaForm m2 = m1.duplicate();
	MetaForm m3 = new MetaForm(m1.sortOf());
	m3.duplicate(m1);

C:	MetaForm m2 = duplicate(m1);
	MetaForm m3 = meta_form(sort_of(m1));
	duplicate_into(m3, m1);

####9. How to output sorts.

Two output forms exists for (composite) sorts: One prints the sort
to standard out, the other provides a string version of the sort.
This string is an exact definition of the sort, as understood
by the "sort/new Sort" function.

Java:	String def = s1.toString();
	s1.print();

C:	StringBuffer out_buffer = string_buffer();
	String out;
	buffer_it(out_buffer, s1);
	out = to_string(out_buffer);
	recycle_buffer(out_buffer);
	print_it(s1);

####10. How to output metaforms.

Two output forms exists for metaforms: One prints the metaform to standard
out, the other provides a string version of the metaform.
It is the purpose that this string version can be exactly understood by the parser.
The print function, on the other hand, adds extra formatting for
a better visual appearance.

Java:	String out = m1.toString();
	m1.print();

C:	StringBuffer out_buffer = string_buffer();
	String out;
	buffer_it(out_buffer, m1);
	out = to_string(out_buffer);
	recycle_buffer(out_buffer);
	print_it(m1);

10. How to recycle individuals, metaforms and composite sorts.

In C, a single function exists to recycle any of these three objects.
Java supports automatic garbage collection, thus, there is no need for recycling!

C:	recycle(i1);
	recycle(m1);
	recycle(s1);


B. Sorts and Metaforms: An extensive inspection.

Both composite sorts and Metaforms are organized as lists.
Composite sorts are composed of simple sorts, metaforms are composed of forms.

1. How to determine the length of a (composite) sort or metaform.

One can check whether a composite sort or metaform is empty,
i.e., it contains no components,
or one can retrieve the exact number of components.
In the case of a metaform, one can also retrieve the total number of individuals,
i.e., the total of the lengths of the component forms.

Java:	int n1, n2;
	if (!m1.empty()) {
		n1 = m1.length();
		if (n1 == s1.length())
			n2 = m1.count();	// the total number of individuals
	}

C:	int n1, n2;
	if (!empty(m1)) {
		n1 = length(m1);
		if (n1 == length(s1))
			n2 = count(m1);	/* the total number of individuals */
	}

2. How to loop through a (composite) sort or metaform.

Each composite sort structure and each metaform structure has a "lead" position,
a position in the list of components, that can be positioned either at the beginning
or at the end of the list, or moved one component forward or backward.
One can query whether the "lead" is at the beginning of the list,
at the end of the list, or beyond the list.

Java:	m1.toBegin();
	while (!m1.beyond()) {
		..
		m1.toNext();
	}
	for (m1.toEnd(); !m1.atBegin(); m1.toPrev()) { .. }
	
	s1.toEnd();
	..
	do {
		s1.toPrev();
		..
	} while (!s1.atBegin())
	for (s1.toBegin(); !s1.atEnd(); s1.toNext()) { .. }

C:	to_begin(m1);
	while (!beyond(m1)) {
		..
		to_next(m1);
	}
	for (to_end(m1); !at_begin(m1); to_prev(m1)) { .. }

	to_end(s1);
	while (!at_begin(s1)) {
		..
		to_prev(s1);
	}
	..
	for (to_begin(s1); !at_end(s1); to_next(s1)) { .. }

3. How to retrieve a component of a (composite) sort or metaform.

The current component, as well as the previous and next components
may be retrieved at any position, unless the queried position is outside the list.
Thus, no previous component can be retrieved at the beginning of the list,
and no next component can be retrieved at the end of the list.
At the "beyond position", only the previous component,
i.e., the last component in the list, can be retrieved.
Independent of the "lead" position, one can always retrieve the first and last
components directly.

Java:	Form f1, f2, f3;
	if (!m1.empty()) {
		f1 = m1.first();
		m1.toBegin();
		if (!m1.atEnd()) {
			m1.toNext();
			f1 = m1.previous();
			f2 = m1.current();
			if (f2 != m1.last())
				f3 = m1.next();
		}
	}

C:	SimpleSort o1, o2, o3;
	if (!empty(s1)) {
		o1 = first(s1);
		to_begin(s1);
		if (!at_end(s1)) {
			to_next(s1);
			o1 = previous(s1);
			o2 = current(s1);
			if (o2 != last(s1))
				o3 = next(s1);
		}
	}

4. How to remove a component from a metaform.

One can delete either the component at the "lead" or current position,
or the component at the next position.

Java:	if (!m1.empty()) {
		m1.toBegin();
		while (!m1.atEnd())
			m1.deleteNext();
		m1.delete();
	}

C:	if (!empty(m1)) {
		to_begin(m1);
		while (!at_end(m1))
			delete_next(m1);
		delete(m1);
	}

5. How to purge a metaform.

Java:	m1.purge();

C:	purge(m1);
