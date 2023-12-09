package ru.esstu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentListXML implements StudentList {
    private List<Student> students;
    private String xmlFileName;


    public StudentListXML(String xmlFileName) {
        this.xmlFileName = xmlFileName + ".xml";
        this.students = new ArrayList<>();
        loadFromXml();
    }


    @Override
    public List<Student> getAll() {
        return students;
    }


    @Override
    public void add(Student student) {
        students.add(student);
        saveToXml();
    }


    @Override
    public Student getById(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    @Override
    public void delete(String id) {
        Student studentToRemove = null;
        for (Student student : students) {
            if (student.getId().equals(id)) {
                studentToRemove = student;
                break;
            }
        }

        if (studentToRemove != null) {
            students.remove(studentToRemove);
            saveToXml();
        }
    }

    @Override
    public void update(Student student) {
        for (Student existingStudent : students) {
            if (existingStudent.getId().equals(student.getId())) {
                existingStudent.setFirstName(student.getFirstName());
                existingStudent.setLastName(student.getLastName());
                existingStudent.setPartonymicName(student.getPartonymicName());
                existingStudent.setGroup(student.getGroup());
                saveToXml();
                return;
            }
        }
    }

    private void loadFromXml() {
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(xmlFileName);


            if (file.exists()) {
                Document document = builder.parse(file);
                NodeList studentNodes = document.getElementsByTagName("student");

                for (int i = 0; i < studentNodes.getLength(); i++) {
                    Element studentElement = (Element) studentNodes.item(i);
                    String id = studentElement.getAttribute("id");
                    String firstName = studentElement.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = studentElement.getElementsByTagName("lastName").item(0).getTextContent();
                    String partonymicName = studentElement.getElementsByTagName("partonymicName").item(0).getTextContent();
                    String group = studentElement.getElementsByTagName("group").item(0).getTextContent();

                    Student student = new Student(id, firstName, lastName, partonymicName, group);
                    students.add(student);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("students");
            document.appendChild(root);

            for (Student student : students) {
                Element studentElement = document.createElement("student");
                studentElement.setAttribute("id", student.getId());

                Element firstNameElement = document.createElement("firstName");
                firstNameElement.appendChild(document.createTextNode(student.getFirstName()));
                studentElement.appendChild(firstNameElement);

                Element lastNameElement = document.createElement("lastName");
                lastNameElement.appendChild(document.createTextNode(student.getLastName()));
                studentElement.appendChild(lastNameElement);

                Element partonymicNameElement = document.createElement("partonymicName");
                partonymicNameElement.appendChild(document.createTextNode(student.getPartonymicName()));
                studentElement.appendChild(partonymicNameElement);

                Element groupElement = document.createElement("group");
                groupElement.appendChild(document.createTextNode(student.getGroup()));
                studentElement.appendChild(groupElement);

                root.appendChild(studentElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(xmlFileName));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}