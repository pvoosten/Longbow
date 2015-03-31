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

package longbow.core;


import longbow.*;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultLongbowFactory implements LongbowFactory {

	private Class<? extends DataNode> dataNodeClass = DefaultDataNode.class;

	private Class<? extends DataWrapper> dataWrapperClass = DefaultDataWrapper.class;

	private Class<? extends Input> inputClass = DefaultInput.class;

	private Class<? extends Output> outputClass = DefaultOutput.class;

	private Class<? extends Runner> runnerClass = DefaultRunner.class;

	private Class<? extends TransformationContext> transformationContextClass = DefaultTransformationContext.class;

	private Class<? extends Workflow> workflowClass = DefaultWorkflow.class;

	public DataNode createDataNode(final Metadata metadata) {
		try {
			synchronized (dataNodeClass) {
				return dataNodeClass.getConstructor(Metadata.class, LongbowFactory.class).newInstance(metadata, this);
			}
		} catch (final Exception e) {
			final String msg = "Error creating data node";
			throw new LongbowException(msg, e);
		}
	}

	public DataWrapper createDataWrapper() {
		try {
			synchronized (dataWrapperClass) {
				return dataWrapperClass.newInstance();
			}
		} catch (final Exception e) {
			final String msg = "Error creating data wrapper";
			throw new LongbowException(msg, e);
		}
	}

	public Input createInput(final TransformationContext context, final Metadata metadata) {
		try {
			synchronized (inputClass) {
				return inputClass.getConstructor(TransformationContext.class, Metadata.class).newInstance(context, metadata);
			}
		} catch (final Exception e) {
			final String msg = "Error creating input";
			throw new LongbowException(msg, e);
		}
	}

	public Output createOutput(final TransformationContext context, final Metadata metadata) {
		try {
			synchronized (outputClass) {
				return outputClass.getConstructor(TransformationContext.class, Metadata.class).newInstance(context, metadata);
			}
		} catch (final Exception e) {
			final String msg = "Error creating output";
			throw new LongbowException(msg, e);
		}
	}

	public Runner createRunner(final Workflow workflow) {
		try {
			synchronized (runnerClass) {
				return runnerClass.getConstructor(Workflow.class).newInstance(workflow);
			}
		} catch (final Exception e) {
			final String msg = "Error creating runner";
			throw new LongbowException(msg, e);
		}
	}

	public TransformationContext createTransformationContext(final Workflow workflow) {
		try {
			synchronized (transformationContextClass) {
				return transformationContextClass.getConstructor(Workflow.class, LongbowFactory.class).newInstance(workflow, this);
			}
		} catch (final Exception e) {
			final String msg = "Error creating transformation context";
			throw new LongbowException(msg, e);
		}
	}

	public Workflow createWorkflow() {
		try {
			synchronized (workflowClass) {
				return workflowClass.getConstructor(LongbowFactory.class).newInstance(this);
			}
		} catch (final Exception e) {
			final String msg = "Error creating workflow";
			throw new LongbowException(msg, e);
		}
	}

	public String getDataNodeClass() {
		return dataNodeClass.getCanonicalName();
	}

	public String getDataWrapperClass() {
		return dataWrapperClass.getCanonicalName();
	}

	public String getInputClass() {
		return inputClass.getCanonicalName();
	}

	public String getOutputClass() {
		return outputClass.getCanonicalName();
	}

	public String getRunnerClass() {
		return runnerClass.getCanonicalName();
	}

	public String getTransformationContextClass() {
		return transformationContextClass.getCanonicalName();
	}

	public String getWorkflowClass() {
		return workflowClass.getCanonicalName();
	}

	public void setDataNodeClass(final String dataNodeClass) {
		synchronized (this.dataNodeClass) {
			this.dataNodeClass = classForName(DataNode.class, dataNodeClass);
		}
	}

	public void setDataWrapperClass(final String dataWrapperClass) {
		synchronized (this.dataWrapperClass) {
			this.dataWrapperClass = classForName(DataWrapper.class, dataWrapperClass);
		}
	}

	public void setInputClass(final String inputClass) {
		synchronized (this.inputClass) {
			this.inputClass = classForName(Input.class, inputClass);
		}
	}

	public void setOutputClass(final String outputClass) {
		synchronized (this.outputClass) {
			this.outputClass = classForName(Output.class, outputClass);
		}
	}

	public void setRunnerClass(final String runnerClass) {
		synchronized (this.runnerClass) {
			this.runnerClass = classForName(Runner.class, runnerClass);
		}
	}

	public void setTransformationContextClass(final String transformationContextClass) {
		synchronized (this.transformationContextClass) {
			this.transformationContextClass = classForName(TransformationContext.class, transformationContextClass);
		}
	}

	public void setWorkflowClass(final String workflowClass) {
		synchronized (this.workflowClass) {
			this.workflowClass = classForName(Workflow.class, workflowClass);
		}
	}

	private <T> Class<? extends T> classForName(final Class<T> superClass, final String className) {
		try {
			final Class<?> cls = Class.forName(className);
			return cls.asSubclass(superClass);
		} catch (final ClassNotFoundException e) {
			throw new LongbowException(e);
		} catch (final ClassCastException e) {
			final String msg = className + " is not a subclass of " + superClass.getCanonicalName();
			throw new LongbowException(msg, e);
		}
	}

}
