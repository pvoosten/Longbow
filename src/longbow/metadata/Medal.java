/* Generated By:JavaCC: Do not edit this line. Medal.java */
/*
 * Copyright 2008 Philip van Oosten (Mentoring Systems BVBA)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package longbow.metadata;


import static longbow.metadata.BinaryOperatorTransformation.*;
import static longbow.metadata.MedalMetadata.*;
import longbow.Metadata;
import longbow.Transformation;
import longbow.TransformationContext;
import longbow.Workflow;
import longbow.core.DefaultWorkflow;
import longbow.transformations.Getter;
import longbow.transformations.Setter;

/**
 * Medal is the Metadata Definition Language, a scripting language to quickly
 * define Metadata for use in Longbow.
 * 
 * @author Philip van Oosten
 * 
 */
@SuppressWarnings("all")
class Medal implements MedalConstants {

	static private int[] jj_la1_0;

	public MedalTokenManager token_source;

	public Token token, jj_nt;

	SimpleCharStream jj_input_stream;

	private Setter dataSetter;

	private Setter metadataSetter;

	private TransformationContext ctxDataSetter;

	private TransformationContext ctxMetadataSetter;

	private Getter acceptDataGetter;

	private Getter acceptsGetter;

	private Getter isAcceptedGetter;

	private Workflow checker;

	private int jj_ntk;

	private int jj_gen;

	final private int[] jj_la1 = new int[6];

	private final java.util.Vector<int[]> jj_expentries = new java.util.Vector<int[]>();

	private int[] jj_expentry;

	private int jj_kind = -1;

	public Medal(final java.io.InputStream stream) {
		this(stream, null);
	}

	public Medal(final java.io.InputStream stream, final String encoding) {
		try {
			jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (final java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source = new MedalTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 6; i++) {
			jj_la1[i] = -1;
		}
	}

	public Medal(final java.io.Reader stream) {
		jj_input_stream = new SimpleCharStream(stream, 1, 1);
		token_source = new MedalTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 6; i++) {
			jj_la1[i] = -1;
		}
	}

	public Medal(final MedalTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 6; i++) {
			jj_la1[i] = -1;
		}
	}

	final public void disable_tracing() {
	}

	final public void enable_tracing() {
	}

	final public TransformationContext Factor() throws ParseException {
		TransformationContext ctx;
		switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
		case LEFT_BRACKET:
			jj_consume_token(LEFT_BRACKET);
			ctx = OrClause();
			jj_consume_token(RIGHT_BRACKET);
			break;
		case NOT:
			ctx = NotClause();
			break;
		case ACCEPT_ALL:
		case ACCEPT_NONE:
		case TYPE_IS:
		case NOT_NULL:
			ctx = LeafClause();
			break;
		default:
			jj_la1[4] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true) {
				return ctx;
			}
		}
		throw new Error("Missing return statement in function");
	}

	public ParseException generateParseException() {
		jj_expentries.removeAllElements();
		final boolean[] la1tokens = new boolean[20];
		for (int i = 0; i < 20; i++) {
			la1tokens[i] = false;
		}
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 6; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & 1 << j) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 20; i++) {
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.addElement(jj_expentry);
			}
		}
		final int[][] exptokseq = new int[jj_expentries.size()][];
		for (int i = 0; i < jj_expentries.size(); i++) {
			exptokseq[i] = (int[]) jj_expentries.elementAt(i);
		}
		return new ParseException(token, exptokseq, tokenImage);
	}

	final public Token getNextToken() {
		if (token.next != null) {
			token = token.next;
		} else {
			token = token.next = token_source.getNextToken();
		}
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	final public Token getToken(final int index) {
		Token t = token;
		for (int i = 0; i < index; i++) {
			if (t.next != null) {
				t = t.next;
			} else {
				t = t.next = token_source.getNextToken();
			}
		}
		return t;
	}

	final public TransformationContext LeafClause() throws ParseException {
		LeafMetadata metadata;
		Token t;
		switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
		case ACCEPT_ALL:
			jj_consume_token(ACCEPT_ALL);
			metadata = acceptAll();
			break;
		case ACCEPT_NONE:
			jj_consume_token(ACCEPT_NONE);
			metadata = acceptNone();
			break;
		case NOT_NULL:
			jj_consume_token(NOT_NULL);
			metadata = notNull();
			break;
		case TYPE_IS:
			jj_consume_token(TYPE_IS);
			t = jj_consume_token(CLASSNAME);
			final Class<?> cls;
			try {
				cls = Class.forName(t.image);
			} catch (final ClassNotFoundException e) {
				{
					if (true) {
						throw new ParseException("The class \"" + t.image + "\" could not be found.");
					}
				}
			}
			metadata = subClassOf(cls);
			break;
		default:
			jj_la1[5] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true) {
				return addLeafMetadata(metadata);
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public TransformationContext NotClause() throws ParseException {
		TransformationContext ctx;
		jj_consume_token(NOT);
		ctx = Factor();
		{
			if (true) {
				return not(ctx);
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public TransformationContext OrClause() throws ParseException {
		TransformationContext ctx;
		TransformationContext tmp;
		ctx = Term();
		switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
		case OR:
		case NOR:
		case XOR:
			switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
			case OR:
				jj_consume_token(OR);
				tmp = OrClause();
				ctx = connectBinaryOperator(ctx, tmp, new OrTransformation());
				break;
			case NOR:
				jj_consume_token(NOR);
				tmp = OrClause();
				ctx = connectBinaryOperator(ctx, tmp, new NorTransformation());
				break;
			case XOR:
				jj_consume_token(XOR);
				tmp = OrClause();
				ctx = connectBinaryOperator(ctx, tmp, new XorTransformation());
				break;
			default:
				jj_la1[0] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
			break;
		default:
			jj_la1[1] = jj_gen;
			;
		}
		{
			if (true) {
				return ctx;
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public void Parse() throws ParseException {
		checker = new DefaultWorkflow();

		dataSetter = new Setter(BootstrapMetadata.getDataMetadata());
		metadataSetter = new Setter(BootstrapMetadata.getMetadataMetadata());
		// inject any kind of metadata, to let metadataSetter be executable
		metadataSetter.inject(BootstrapMetadata.getMetadataMetadata()); // :) ;)

		ctxDataSetter = checker.add(dataSetter);
		ctxMetadataSetter = checker.add(metadataSetter);

		TransformationContext ctx;
		ctx = OrClause();
		jj_consume_token(0);
		acceptDataGetter = new Getter(BootstrapMetadata.getBooleanMetadata());
		acceptsGetter = new Getter(BootstrapMetadata.getBooleanMetadata());
		isAcceptedGetter = new Getter(BootstrapMetadata.getBooleanMetadata());

		final TransformationContext ctxAcceptDataGetter = checker.add(acceptDataGetter);
		final TransformationContext ctxAcceptsGetter = checker.add(acceptsGetter);
		final TransformationContext ctxIsAcceptedGetter = checker.add(isAcceptedGetter);

		if (!checker.connect(ctx, OUT_ACCEPT_DATA, ctxAcceptDataGetter, Getter.IN_DATA)) {
			if (true) {
				throw new ParseException("Can't connect getter");
			}
		}
		if (!checker.connect(ctx, OUT_ACCEPT_METADATA, ctxAcceptsGetter, Getter.IN_DATA)) {
			if (true) {
				throw new ParseException("Can't connect getter");
			}
		}
		if (!checker.connect(ctx, OUT_IS_METADATA_ACCEPTED, ctxIsAcceptedGetter, Getter.IN_DATA)) {
			if (true) {
				throw new ParseException("Can't connect getter");
			}
		}

		if (!checker.isExecutable()) {
			{
				if (true) {
					throw new ParseException("The metadata checker is not executable.");
				}
			}
		}
		checker.execute();
	}

	public void ReInit(final java.io.InputStream stream) {
		ReInit(stream, null);
	}

	public void ReInit(final java.io.InputStream stream, final String encoding) {
		try {
			jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (final java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 6; i++) {
			jj_la1[i] = -1;
		}
	}

	public void ReInit(final java.io.Reader stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 6; i++) {
			jj_la1[i] = -1;
		}
	}

	public void ReInit(final MedalTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 6; i++) {
			jj_la1[i] = -1;
		}
	}

	final public TransformationContext Term() throws ParseException {
		TransformationContext ctx;
		TransformationContext tmp;
		ctx = Factor();
		label_1: while (true) {
			switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
			case AND:
			case NAND:
				;
				break;
			default:
				jj_la1[2] = jj_gen;
				break label_1;
			}
			switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
			case AND:
				jj_consume_token(AND);
				tmp = Factor();
				ctx = connectBinaryOperator(ctx, tmp, new AndTransformation());
				break;
			case NAND:
				jj_consume_token(NAND);
				tmp = Factor();
				ctx = connectBinaryOperator(ctx, tmp, new NandTransformation());
				break;
			default:
				jj_la1[3] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		{
			if (true) {
				return ctx;
			}
		}
		throw new Error("Missing return statement in function");
	}

	TransformationContext addLeafMetadata(final Metadata metadata) throws ParseException {
		final TransformationContext ctx = checker.add(new MetadataTransformation(metadata));
		if (!checker.connect(ctxDataSetter, Setter.OUT_DATA, ctx, MetadataTransformation.IN_DATA)) {
			throw new ParseException("Can't connect data setter");
		}
		if (!checker.connect(ctxMetadataSetter, Setter.OUT_DATA, ctx, MetadataTransformation.IN_METADATA)) {
			throw new ParseException("Can't connect metadata setter");
		}
		return ctx;
	}

	Getter getAcceptDataGetter() {
		return acceptDataGetter;
	}

	Getter getAcceptsMetadataGetter() {
		return acceptsGetter;
	}

	/**
	 * A reference to the workflow is necessary to avoid GC of the workflow.
	 */
	Workflow getChecker() {
		return checker;
	}

	Setter getDataSetter() {
		return dataSetter;
	}

	Getter getIsAcceptedGetter() {
		return isAcceptedGetter;
	}

	Setter getMetadataSetter() {
		return metadataSetter;
	}

	private TransformationContext connectBinaryOperator(final TransformationContext former, final TransformationContext latter, final Transformation operator) throws ParseException {
		final TransformationContext ctxOperator = checker.add(operator);
		if (!checker.connect(former, OUT_ACCEPT_DATA, ctxOperator, IN_FORMER_ACCEPT_DATA)) {
			throw new ParseException("Can't connect operator");
		}
		if (!checker.connect(former, OUT_ACCEPT_METADATA, ctxOperator, IN_FORMER_ACCEPT_METADATA)) {
			throw new ParseException("Can't connect operator");
		}
		if (!checker.connect(former, OUT_IS_METADATA_ACCEPTED, ctxOperator, IN_FORMER_IS_METADATA_ACCEPTED)) {
			throw new ParseException("Can't connect operator");
		}
		if (!checker.connect(latter, OUT_ACCEPT_DATA, ctxOperator, IN_LATTER_ACCEPT_DATA)) {
			throw new ParseException("Can't connect operator");
		}
		if (!checker.connect(latter, OUT_ACCEPT_METADATA, ctxOperator, IN_LATTER_ACCEPT_METADATA)) {
			throw new ParseException("Can't connect operator");
		}
		if (!checker.connect(latter, OUT_IS_METADATA_ACCEPTED, ctxOperator, IN_LATTER_IS_METADATA_ACCEPTED)) {
			throw new ParseException("Can't connect operator");
		}
		return ctxOperator;
	}

	final private Token jj_consume_token(final int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = token).next != null) {
			token = token.next;
		} else {
			token = token.next = token_source.getNextToken();
		}
		jj_ntk = -1;
		if (token.kind == kind) {
			jj_gen++;
			return token;
		}
		token = oldToken;
		jj_kind = kind;
		throw generateParseException();
	}

	final private int jj_ntk() {
		if ((jj_nt = token.next) == null) {
			return jj_ntk = (token.next = token_source.getNextToken()).kind;
		} else {
			return jj_ntk = jj_nt.kind;
		}
	}

	private TransformationContext not(final TransformationContext ctx) throws ParseException {
		final TransformationContext operator = checker.add(new NotTransformation());
		// connect the not-transformation to the outputs of ctx
		if (!checker.connect(ctx, OUT_ACCEPT_DATA, operator, NotTransformation.IN_ACCEPT_DATA)) {
			throw new ParseException("Can't connect not operator");
		}
		if (!checker.connect(ctx, OUT_ACCEPT_METADATA, operator, NotTransformation.IN_ACCEPT_METADATA)) {
			throw new ParseException("Can't connect not operator");
		}
		if (!checker.connect(ctx, OUT_IS_METADATA_ACCEPTED, operator, NotTransformation.IN_IS_METADATA_ACCEPTED)) {
			throw new ParseException("Can't connect not operator");
		}
		return operator;
	}

	private static void jj_la1_0() {
		jj_la1_0 = new int[] { 0x3400, 0x3400, 0xa00, 0xa00, 0x7c080, 0x78000, };
	}

	static {
		jj_la1_0();
	}

}
