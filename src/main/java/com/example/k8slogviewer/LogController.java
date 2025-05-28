package com.example.k8slogviewer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LogController {
    @GetMapping("/")
    public String index(Model model) {
        List<String> namespaces = getNamespaces();
        model.addAttribute("namespaces", namespaces);
        return "index";
    }

    @GetMapping("/pods")
    public String getPods(@RequestParam String namespace, Model model) {
        List<String> pods = getPodsForNamespace(namespace);
        model.addAttribute("pods", pods);
        model.addAttribute("namespace", namespace);
        return "pods";
    }

    @PostMapping("/logs")
    public String getLogs(@RequestParam String namespace, @RequestParam String pod, @RequestParam(required = false, defaultValue = "100") int lineCount, Model model) {
        String logs = getPodLogs(namespace, pod, lineCount);
        model.addAttribute("logs", logs);
        model.addAttribute("namespace", namespace);
        model.addAttribute("pod", pod);
        model.addAttribute("lineCount", lineCount);
        return "logs";
    }

    @GetMapping("/api/pods")
    @ResponseBody
    public List<String> apiGetPods(@RequestParam String namespace) {
        return getPodsForNamespace(namespace);
    }

    private List<String> getNamespaces() {
        List<String> namespaces = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("kubectl", "get", "namespaces", "-o", "name").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                namespaces.add(line.replace("namespace/", ""));
            }
        } catch (Exception e) {
            namespaces.add("default");
        }
        return namespaces;
    }

    private List<String> getPodsForNamespace(String namespace) {
        List<String> pods = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("kubectl", "get", "pods", "-n", namespace, "-o", "name").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                pods.add(line.replace("pod/", ""));
            }
        } catch (Exception e) {
            pods.add("No pods found");
        }
        return pods;
    }

    private String getPodLogs(String namespace, String pod, int lineCount) {
        StringBuilder logs = new StringBuilder();
        try {
            Process process = new ProcessBuilder("kubectl", "logs", "-n", namespace, pod, "--tail=" + lineCount).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }
        } catch (Exception e) {
            logs.append("Failed to get logs");
        }
        return logs.toString();
    }
} 