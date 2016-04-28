package com.kronos.telestaff.autotest.xml.reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLDataParser {

		public static final String LIST_NODE_NAME = "list";
		public static final String CONTAINER_NODE_NAME = "container";
		public static final String FIELD_NODE_NAME = "field";
		public static final String NAME_ATTRIBUTE_NAME = "name";
		public static final String VALUE_ATTRIBUTE_NAME = "value";
		public static final String TYPE_ATTRIBUTE_NAME = "type";
		public static final String LINK_ATTRIBUTE_NAME = "link";
		public static final String BUILDER_CLASS_ATTRIBUTE_NAME = "builderClass";
		public static final String BUILDER_ARGS_ATTRIBUTE_NAME = "builderArgs";

		/**
		 * Parses an XML file into a XMLDataContainer
		 * 
		 * @param xmlPath
		 *            The relative or absolute path to an XML file
		 * @return A fully populated XMLDataContainer that includes the root XML
		 *         element.
		 */
		public IntDataContainer parseXML(String xmlPath) {
			Document document = getXMLDocument(xmlPath);
			IntDataContainer rootContainer = createContainer(document
					.getDocumentElement());
			((DataContainer) rootContainer).resolveAllLinks();
			return rootContainer;
		}

		/**
		 * Opens the XML document for DOM parsing
		 * 
		 * @param xmlPath
		 * @return
		 */
		private Document getXMLDocument(String xmlPath) {
			xmlPath = validateFilePath(xmlPath);
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder documentBuilder = factory.newDocumentBuilder();
				Document document = documentBuilder.parse(xmlPath);
				return document;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}

		protected String validateFilePath(String filePath) {
			return FileLocator.validateFilePath(filePath);
		}

		/**
		 * Creates and recursively populates a XMLDataContainer based on the
		 * specified XML node
		 * 
		 * @param node
		 *            A node from an XML file, should be a <container> node that
		 *            specifies a name="" attribute
		 * @return A XMLDataContainer that is populated with the XML node and it's
		 *         child nodes.
		 */
		private IntDataContainer createContainer(Node node) {
			if (isDynamicContainer(node) == true) {
				return createDynamicContainer(node);
			} else {
				return createStaticContainer(node);
			}
		}

		/**
		 * Checks whether the container specifies a builder class
		 * 
		 * @param node
		 * @return
		 */
		private boolean isDynamicContainer(Node node) {
			if (getAttributeValue(node, BUILDER_CLASS_ATTRIBUTE_NAME) == null) {
				return false;
			} else {
				return true;
			}
		}

		/**
		 * Copies data from XML nodes into data containers, lists and fields
		 * 
		 * Calls to createContainer can recurse back into this method
		 * 
		 * @param node
		 * @return
		 */
		private IntDataContainer createStaticContainer(Node node) {
			if (isContainerNode(node) == false) {
				return null;
			}

			String containerName = getAttributeValue(node, NAME_ATTRIBUTE_NAME);
			DataContainer newContainer = new DataContainer(containerName);

			Node childNode = null;
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				childNode = childNodes.item(i);
				if (isFieldNode(childNode) == true) {
					IntContainerField newField = createField(childNode);
					newContainer.setField(newField);
				}
				if (isContainerNode(childNode)) {
					IntDataContainer childContainer = createContainer(childNode);
					newContainer.addDataContainer(childContainer);
				}
				if (isListNode(childNode)) {
					ContainerList newList = createList(childNode);
					newContainer.addList(newList);
				}
			}
			return newContainer;

		}

		/**
		 * For dynamic containers, a builder class is created, and used to inject a
		 * container here from another source.
		 * 
		 * Note: the builder class used may create another instance of the
		 * XMLDataParser if XML documents are linked to other XML documents
		 * 
		 * @param node
		 * @return
		 */
		private IntDataContainer createDynamicContainer(Node node) {
			String containerName = getAttributeValue(node, NAME_ATTRIBUTE_NAME);
			String builderClassName = getAttributeValue(node,
					BUILDER_CLASS_ATTRIBUTE_NAME);
			String builderArgs = getAttributeValue(node,
					BUILDER_ARGS_ATTRIBUTE_NAME);
			Class<?> builderClass = getBuilderClass(builderClassName);
			IntContainerBuilder containerBuilder = createContainerBuilder(builderClass);
			DataContainer newContainer = (DataContainer) containerBuilder
					.buildContainer(builderArgs);
			newContainer.setName(containerName);
			return newContainer;
		}

		/**
		 * Loads the specified class by name using the classloader that loaded this
		 * class
		 * 
		 * @param builderClassName
		 * @return
		 */
		private Class<?> getBuilderClass(String builderClassName) {
			try {
				ClassLoader classLoader = this.getClass().getClassLoader();
				Class<?> builderClass = classLoader.loadClass(builderClassName);
				return builderClass;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Creates an instance of the specified container builder class
		 * 
		 * @param builderClass
		 * @return
		 */
		private IntContainerBuilder createContainerBuilder(Class<?> builderClass) {
			try {
				IntContainerBuilder containerBuilder = (IntContainerBuilder) builderClass
						.newInstance();
				return containerBuilder;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}

		}

		/**
		 * Determines whether field is a link or static field, and creates a new
		 * field object accordingly.
		 * 
		 * @param node
		 *            A <field> node from an XML document
		 * @return
		 */
		private IntContainerField createField(Node node) {
			if (fieldIsALink(node) == true) {
				return createLinkField(node);
			} else if (fieldIsStatic(node) == true) {
				return createStaticField(node);
			}
			throw new RuntimeException(
					"Fields must specify either a link or a value attribute.");
		}

		/**
		 * Checks the given node for the "link" attribute
		 * 
		 * @param node
		 * @return
		 */
		private boolean fieldIsALink(Node node) {
			if (getAttributeValue(node, LINK_ATTRIBUTE_NAME) == null) {
				return false;
			} else {
				return true;
			}
		}

		/**
		 * Checks the given node for the "value" attribute
		 * 
		 * @param node
		 * @return
		 */
		private boolean fieldIsStatic(Node node) {
			if (getAttributeValue(node, VALUE_ATTRIBUTE_NAME) == null) {
				return false;
			} else {
				return true;
			}
		}

		/**
		 * Creates a field that links to another field
		 * 
		 * Links need to be resolved before the datacontainer is returned to the
		 * user
		 * 
		 * @param node
		 * @return
		 */
		private IntContainerField createLinkField(Node node) {
			String fieldName = getAttributeValue(node, NAME_ATTRIBUTE_NAME);
			String linkValue = getAttributeValue(node, LINK_ATTRIBUTE_NAME);
			ContainerField newField = new ContainerField(fieldName);
			newField.setLink(linkValue);
			return newField;
		}

		/**
		 * Creates a static field from the given field node.
		 * 
		 * @param node
		 * @return
		 */
		private IntContainerField createStaticField(Node node) {
			String fieldName = getAttributeValue(node, NAME_ATTRIBUTE_NAME);
			String fieldValue = getAttributeValue(node, VALUE_ATTRIBUTE_NAME);
			String fieldType = getAttributeValue(node, TYPE_ATTRIBUTE_NAME);
			IntContainerField newField = new ContainerField(fieldName, fieldValue, fieldType);
			return newField;
		}

		/**
		 * Checks if the given node is a container node
		 * 
		 * @param node
		 * @return
		 */
		private boolean isContainerNode(Node node) {
			String nodeName = node.getNodeName();
			return nodeName.equalsIgnoreCase(CONTAINER_NODE_NAME);
		}

		/**
		 * Checks if the given node is a field node
		 * 
		 * @param node
		 * @return
		 */
		private boolean isFieldNode(Node node) {
			String nodeName = node.getNodeName();
			return nodeName.equalsIgnoreCase(FIELD_NODE_NAME);
		}

		/**
		 * Checks if the given node is a list node
		 * 
		 * @param node
		 * @return
		 */
		private boolean isListNode(Node node) {
			String nodeName = node.getNodeName();
			return nodeName.equalsIgnoreCase(LIST_NODE_NAME);
		}

		/**
		 * Helper Method: gets the specified attribute fromt the given node
		 * 
		 * @param node
		 * @param attributeName
		 * @return
		 */
		private String getAttributeValue(Node node, String attributeName) {
			NamedNodeMap attributes = node.getAttributes();
			Node attribute = attributes.getNamedItem(attributeName);
			if (attribute == null) {
				return null;
			} else {
				String attributeValue = attribute.getNodeValue();
				return attributeValue;
			}
		}

		/**
		 * Creates a XMLContainerList from the specified XML node, and populates the
		 * list. This method uses createContainer() to recursively populate all of
		 * the child containers and child lists of this node as well.
		 * 
		 * @param node
		 *            An XML node of type <list> which specifies the name=""
		 *            attribute
		 * @return A fully populated XMLContainerList with all children included
		 */
		private ContainerList createList(Node node) {
			NodeList nodes = node.getChildNodes();
			String listName = getAttributeValue(node, NAME_ATTRIBUTE_NAME);

			ContainerList list = new ContainerList(listName);
			Node childNode = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				// TODO: Just to verify, are we skipping the XML TYPE root node,
				// here?
				childNode = nodes.item(i);
				if (childNode.getNodeName().equals(CONTAINER_NODE_NAME)) {
					IntDataContainer container = createContainer(childNode);
					list.addContainer(container);
				}
			}
			return list;
		}


	}



