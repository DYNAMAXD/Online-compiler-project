package com.online.compiler.project.project;    
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
public class CompileController {

    @PostMapping("/api/compile")
    public String compile(@RequestBody String code) throws Exception {
        Path dir = Files.createTempDirectory("c_");
        Path src = dir.resolve("main.c");
        Files.writeString(src, code);

        Process compile = new ProcessBuilder("gcc", "main.c", "-o", "main")
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .start();
        String compileOut = readOutput(compile);
        if (!compile.waitFor(10, TimeUnit.SECONDS) || compile.exitValue() != 0) {
            return "Compile Error:\n" + compileOut;
        }

        Process run = new ProcessBuilder("./main")
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .start();
        String runOut = readOutput(run);
        run.waitFor(5, TimeUnit.SECONDS);
        return runOut;
    }

    private String readOutput(Process p) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }
}