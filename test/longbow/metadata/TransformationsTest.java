package longbow.metadata;


import static java.lang.Boolean.*;
import static junit.framework.Assert.*;
import longbow.TransformationContext;
import longbow.Workflow;
import longbow.core.DefaultWorkflow;
import longbow.transformations.Getter;
import longbow.transformations.Setter;

import org.junit.Test;

public class TransformationsTest {

	private Workflow workflow;

	private Setter formerData, formerAcc, formerIsAcc, latterData, latterAcc, latterIsAcc;

	private Getter data, acc, isAcc;

	public void setup(final BinaryOperatorTransformation transformation) throws AssertionError {
		formerData = new Setter(BootstrapMetadata.getBooleanMetadata());
		formerAcc = new Setter(BootstrapMetadata.getBooleanMetadata());
		formerIsAcc = new Setter(BootstrapMetadata.getBooleanMetadata());
		latterData = new Setter(BootstrapMetadata.getBooleanMetadata());
		latterAcc = new Setter(BootstrapMetadata.getBooleanMetadata());
		latterIsAcc = new Setter(BootstrapMetadata.getBooleanMetadata());
		data = new Getter(BootstrapMetadata.getBooleanMetadata());
		acc = new Getter(BootstrapMetadata.getBooleanMetadata());
		isAcc = new Getter(BootstrapMetadata.getBooleanMetadata());
		workflow = new DefaultWorkflow();
		final TransformationContext ctxFormerData = workflow.add(formerData);
		final TransformationContext ctxFormerAcc = workflow.add(formerAcc);
		final TransformationContext ctxFormerIsAcc = workflow.add(formerIsAcc);
		final TransformationContext ctxLatterData = workflow.add(latterData);
		final TransformationContext ctxLatterAcc = workflow.add(latterAcc);
		final TransformationContext ctxLatterIsAcc = workflow.add(latterIsAcc);
		final TransformationContext ctxData = workflow.add(data);
		final TransformationContext ctxAcc = workflow.add(acc);
		final TransformationContext ctxIsAcc = workflow.add(isAcc);
		final TransformationContext ctxTransformation = workflow.add(transformation);
		assertTrue(workflow.connect(ctxFormerAcc, Setter.OUT_DATA, ctxTransformation, BinaryOperatorTransformation.IN_FORMER_ACCEPT_METADATA));
		assertTrue(workflow.connect(ctxFormerData, Setter.OUT_DATA, ctxTransformation, BinaryOperatorTransformation.IN_FORMER_ACCEPT_DATA));
		assertTrue(workflow.connect(ctxFormerIsAcc, Setter.OUT_DATA, ctxTransformation, BinaryOperatorTransformation.IN_FORMER_IS_METADATA_ACCEPTED));
		assertTrue(workflow.connect(ctxLatterAcc, Setter.OUT_DATA, ctxTransformation, BinaryOperatorTransformation.IN_LATTER_ACCEPT_METADATA));
		assertTrue(workflow.connect(ctxLatterData, Setter.OUT_DATA, ctxTransformation, BinaryOperatorTransformation.IN_LATTER_ACCEPT_DATA));
		assertTrue(workflow.connect(ctxLatterIsAcc, Setter.OUT_DATA, ctxTransformation, BinaryOperatorTransformation.IN_LATTER_IS_METADATA_ACCEPTED));
		assertTrue(workflow.connect(ctxTransformation, BinaryOperatorTransformation.OUT_ACCEPT_DATA, ctxData, Getter.IN_DATA));
		assertTrue(workflow.connect(ctxTransformation, BinaryOperatorTransformation.OUT_ACCEPT_METADATA, ctxAcc, Getter.IN_DATA));
		assertTrue(workflow.connect(ctxTransformation, BinaryOperatorTransformation.OUT_IS_METADATA_ACCEPTED, ctxIsAcc, Getter.IN_DATA));
		inject(TRUE, TRUE);
		assertTrue("Can't execute workflow", workflow.execute());
	}

	@Test
	public void testAnd() {
		setup(new AndTransformation());
		assertBinaryOperation(TRUE, TRUE, TRUE);
		assertBinaryOperation(TRUE, FALSE, FALSE);
		assertBinaryOperation(FALSE, TRUE, FALSE);
		assertBinaryOperation(FALSE, FALSE, FALSE);
	}

	@Test
	public void testNand() {
		setup(new NandTransformation());
		assertBinaryOperation(TRUE, TRUE, FALSE);
		assertBinaryOperation(TRUE, FALSE, TRUE);
		assertBinaryOperation(FALSE, TRUE, TRUE);
		assertBinaryOperation(FALSE, FALSE, TRUE);
	}

	@Test
	public void testNor() {
		setup(new NorTransformation());
		assertBinaryOperation(TRUE, TRUE, FALSE);
		assertBinaryOperation(TRUE, FALSE, FALSE);
		assertBinaryOperation(FALSE, TRUE, FALSE);
		assertBinaryOperation(FALSE, FALSE, TRUE);
	}

	@Test
	public void testOr() {
		setup(new OrTransformation());
		assertBinaryOperation(TRUE, TRUE, TRUE);
		assertBinaryOperation(TRUE, FALSE, TRUE);
		assertBinaryOperation(FALSE, TRUE, TRUE);
		assertBinaryOperation(FALSE, FALSE, FALSE);
	}

	@Test
	public void testXor() {
		setup(new XorTransformation());
		assertBinaryOperation(TRUE, TRUE, FALSE);
		assertBinaryOperation(TRUE, FALSE, TRUE);
		assertBinaryOperation(FALSE, TRUE, TRUE);
		assertBinaryOperation(FALSE, FALSE, FALSE);
	}

	private void assertBinaryOperation(final Boolean former, final Boolean latter, final Boolean gdata) throws AssertionError {
		inject(former, latter);
		assertEquals(gdata, data.extract());
		assertEquals(gdata, acc.extract());
		assertEquals(gdata, isAcc.extract());
	}

	private void inject(final Boolean former, final Boolean latter) {
		formerData.inject(former);
		formerAcc.inject(former);
		formerIsAcc.inject(former);
		latterData.inject(latter);
		latterAcc.inject(latter);
		latterIsAcc.inject(latter);
	}

}
