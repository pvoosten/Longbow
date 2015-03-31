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

package longbow.test.checker;


import static longbow.test.checker.Checker.LogicalOperator.*;

import java.util.Arrays;

/**
 * Checks if preset conditions about the static model are met after
 * transformation.
 * 
 * @author Philip van Oosten
 * 
 */
public abstract class Checker {

	static final Checker NULL = new NullChecker();

	static final Checker FALSE = new FalseChecker();

	LogicalOperator logicalOperator;

	Checker() {
		logicalOperator = NOP;
	}

	public Checker and(final Checker composed) {
		return new BinaryChecker(this, composed, AND);
	}

	public boolean check(final Object data) {
		return doCheck(data);
	}

	public Checker nand(final Checker composed) {
		return new BinaryChecker(this, composed, NAND);
	}

	public Checker nor(final Checker composed) {
		return new BinaryChecker(this, composed, NOR);
	}

	public Checker or(final Checker composed) {
		return new BinaryChecker(this, composed, OR);
	}

	@Override
	public abstract String toString();

	public Checker xor(final Checker composed) {
		return new BinaryChecker(this, composed, XOR);
	}

	abstract protected boolean doCheck(final Object data);

	public static Checker isA(final Class<?> clazz) {
		return new Checker() {

			@Override
			public String toString() {
				return "IS A " + clazz.getCanonicalName();
			}

			@Override
			protected boolean doCheck(final Object data) {
				return clazz.isAssignableFrom(data.getClass());
			}

		};
	}

	public static Checker not(final Checker checker) {
		return new NotChecker(checker);
	}

	public static Checker valueEquals(final Object value) {
		return new Checker() {

			@Override
			public String toString() {
				return "equals (" + (value == null ? "null" : value.toString()) + ")";
			}

			@Override
			protected boolean doCheck(final Object data) {
				return data.equals(value);
			}
		};
	}

	public static Checker valuesEqual(final Object... values) {
		return new Checker() {

			@Override
			public String toString() {
				return "values equal " + Arrays.toString(values);
			}

			@Override
			protected boolean doCheck(final Object data) {
				if (!(data instanceof Iterable)) {
					return false;
				}
				int i = 0;
				for (final Object obj : (Iterable<?>) data) {
					if (!values[i].equals(obj)) {
						return false;
					}
					i++;
				}
				return true;

			}
		};

	}

	static class BinaryChecker extends Checker {

		private final Checker former;

		private final Checker latter;

		public BinaryChecker(final Checker former, final Checker latter, final LogicalOperator operator) {
			this.former = former;
			this.latter = latter;
			logicalOperator = operator;
		}

		@Override
		public String toString() {
			return "(" + former.toString() + ") " + logicalOperator.toString() + " (" + latter.toString() + ")";
		}

		@Override
		protected boolean doCheck(final Object data) {
			return logicalOperator.logicalOperator(former.doCheck(data), latter.doCheck(data));
		}
	}

	static class FalseChecker extends Checker {

		@Override
		public boolean check(final Object data) {
			return false;
		}

		@Override
		public String toString() {
			return "FALSE";
		}

		@Override
		protected boolean doCheck(final Object data) {
			return false;
		}

	}

	enum LogicalOperator {
		AND {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return former && latter;
			}
		},
		OR {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return former || latter;
			}
		},
		NOT {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return !latter;
			}
		},
		NAND {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return !former || !latter;
			}
		},

		NOR {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return !former && !latter;
			}
		},

		XOR {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return former && !latter || !former && latter;
			}
		},

		NOP {

			@Override
			boolean logicalOperator(final boolean former, final boolean latter) {
				return true;
			}
		};

		abstract boolean logicalOperator(boolean former, boolean latter);
	}

	static class NotChecker extends Checker {

		private final Checker operand;

		NotChecker(final Checker operand) {
			this.operand = operand;
		}

		@Override
		public String toString() {
			return "NOT (" + operand.toString() + ")";
		}

		@Override
		protected boolean doCheck(final Object data) {
			return NOT.logicalOperator(false, operand.doCheck(data));
		}
	}

	static class NullChecker extends Checker {

		@Override
		public boolean check(final Object data) throws AssertionError {
			return true;
		}

		@Override
		public String toString() {
			return "TRUE";
		}

		@Override
		protected boolean doCheck(final Object data) {
			return true;
		}

	}

}
