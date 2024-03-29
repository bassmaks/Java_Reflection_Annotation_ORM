package edu.school21.app;
import com.google.auto.service.AutoService;
import edu.school21.annotation.HtmlForm;
import edu.school21.annotation.HtmlInput;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("edu.school21.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HtmlProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element cls : roundEnv.getElementsAnnotatedWith(HtmlForm.class)) {
            List<String> content = new ArrayList<>();

            HtmlForm htmlForm = cls.getAnnotation(HtmlForm.class);
            String fileName = htmlForm.fileName();
            content.add(
                    new StringBuilder()
                            .append("<form action = \"")
                            .append(htmlForm.action())
                            .append("\" method = \"")
                            .append(htmlForm.method())
                            .append("\">").
                            toString()
            );
            List<? extends Element> clsElems = cls.getEnclosedElements();
            for (Element field : roundEnv.getElementsAnnotatedWith(HtmlInput.class)) {
                if (!clsElems.contains(field)) continue;
                HtmlInput htmlInput = field.getAnnotation(HtmlInput.class);
                content.add(
                        new StringBuilder()
                                .append("\t<input type = \"")
                                .append(htmlInput.type())
                                .append("\" name = \"")
                                .append(htmlInput.name())
                                .append("\" placeholder = \"")
                                .append(htmlInput.placeholder())
                                .append("\">")
                                .toString()
                );
            }

            content.add("\t<input type = \"submit\" value = \"Send\">");
            content.add("</form>");
            writeToFile(fileName, content);
        }
        return true;
    }

        private void writeToFile(String fileName, List<String> content){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("target/classes/" + fileName))) {
                for (String line : content){
                    writer.write(line);
                    writer.write("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}

