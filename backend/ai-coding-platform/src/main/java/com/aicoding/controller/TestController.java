package com.aicoding.controller;

import com.aicoding.config.AIProblemYamlConfig;
import com.aicoding.model.AIProblem;
import com.aicoding.model.Difficulty;
import com.aicoding.repository.AIProblemRepository;
import com.aicoding.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private AIProblemRepository aiProblemRepository;

    @Autowired
    private DataInitializationService dataInitializationService;

    @Autowired
    private AIProblemYamlConfig aiProblemYamlConfig;

    @GetMapping("/mongodb-status")
    public ResponseEntity<Map<String, Object>> getMongoDBStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            long count = aiProblemRepository.count();
            status.put("connected", true);
            status.put("totalProblems", count);
            status.put("message", "MongoDB connection successful");
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("connected", false);
            status.put("error", e.getMessage());
            return ResponseEntity.status(500).body(status);
        }
    }

    @GetMapping("/problems")
    public ResponseEntity<List<AIProblem>> getAllProblems() {
        List<AIProblem> problems = aiProblemRepository.findAll();
        return ResponseEntity.ok(problems);
    }

    @PostMapping("/migrate-data")
    public ResponseEntity<Map<String, Object>> migrateData() {
        Map<String, Object> response = new HashMap<>();
        try {
            dataInitializationService.initializeAIProblemsFromConfig();
            response.put("success", true);
            response.put("message", "Data migration completed successfully");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/clear-data")
    public ResponseEntity<Map<String, Object>> clearData() {
        Map<String, Object> response = new HashMap<>();
        try {
            aiProblemRepository.deleteAll();
            response.put("success", true);
            response.put("message", "All data cleared successfully");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/migration-status")
    public ResponseEntity<Map<String, Object>> getMigrationStatus() {
        Map<String, Object> status = new HashMap<>();
        long totalCount = aiProblemRepository.count();
        long simpleCodingCount = aiProblemRepository.findByActiveTrueAndCategory("Simple AI Coding").size();
        long debuggingCount = aiProblemRepository.findByActiveTrueAndCategory("AI Debugging").size();
        long systemDesignCount = aiProblemRepository.findByActiveTrueAndCategory("AI System Design").size();
        long advancedCount = aiProblemRepository.findByActiveTrueAndCategory("Advanced AI Challenges").size();
        
        status.put("totalProblems", totalCount);
        status.put("simpleCoding", simpleCodingCount);
        status.put("debugging", debuggingCount);
        status.put("systemDesign", systemDesignCount);
        status.put("advanced", advancedCount);
        status.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(status);
    }

    @PostMapping("/reinitialize-data")
    public ResponseEntity<Map<String, Object>> reinitializeData() {
        Map<String, Object> response = new HashMap<>();
        try {
            dataInitializationService.initializeAIProblemsFromConfig();
            response.put("success", true);
            response.put("message", "Data reinitialization completed successfully");
            response.put("totalProblems", aiProblemRepository.count());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/add-simple-coding-questions")
    public ResponseEntity<Map<String, Object>> addSimpleCodingQuestions() {
        return createCategoryQuestions("Simple AI Coding", this::createSimpleAICodingQuestions);
    }

    @PostMapping("/add-debugging-questions")
    public ResponseEntity<Map<String, Object>> addDebuggingQuestions() {
        return createCategoryQuestions("AI Debugging", this::createAIDebuggingQuestions);
    }

    @PostMapping("/add-system-design-questions")
    public ResponseEntity<Map<String, Object>> addSystemDesignQuestions() {
        return createCategoryQuestions("AI System Design", this::createAISystemDesignQuestions);
    }

    @PostMapping("/add-advanced-questions")
    public ResponseEntity<Map<String, Object>> addAdvancedQuestions() {
        return createCategoryQuestions("Advanced AI Challenges", this::createAdvancedAIChallengesQuestions);
    }

    @PostMapping("/load-from-yaml")
    public ResponseEntity<Map<String, Object>> loadProblemsFromYaml() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Clear existing data
            aiProblemRepository.deleteAll();
            
            // Load problems from YAML configuration
            List<AIProblem> problems = new ArrayList<>();
            Map<String, Map<String, Object>> problemsMap = aiProblemYamlConfig.getProblems();
            
            for (Map.Entry<String, Map<String, Object>> entry : problemsMap.entrySet()) {
                Map<String, Object> problemConfig = entry.getValue();
                AIProblem problem = new AIProblem();
                problem.setTitle((String) problemConfig.get("title"));
                problem.setDescription((String) problemConfig.get("description"));
                problem.setDifficulty(Difficulty.valueOf(((String) problemConfig.get("difficulty")).toUpperCase()));
                problem.setCategory((String) problemConfig.get("category"));
                problem.setTags(problemConfig.get("tags") != null ? (List<String>) problemConfig.get("tags") : new ArrayList<>());
                
                // Handle estimatedTime - convert to String if it's an Integer
                Object estimatedTimeObj = problemConfig.get("estimatedTime");
                if (estimatedTimeObj instanceof Integer) {
                    problem.setEstimatedTime(String.valueOf(estimatedTimeObj));
                } else {
                    problem.setEstimatedTime((String) estimatedTimeObj);
                }
                
                // Handle acceptanceRate - convert to String if it's a Number
                Object acceptanceRateObj = problemConfig.get("acceptanceRate");
                if (acceptanceRateObj instanceof Number) {
                    problem.setAcceptanceRate(String.valueOf(acceptanceRateObj));
                } else {
                    problem.setAcceptanceRate((String) acceptanceRateObj);
                }
                
                problem.setStarterCode((String) problemConfig.get("starterCode"));
                problem.setActive(true);
                problem.setCreatedAt(LocalDateTime.now());
                problem.setUpdatedAt(LocalDateTime.now());
                
                // Convert test cases
                List<AIProblem.TestCase> testCases = new ArrayList<>();
                if (problemConfig.get("testCases") != null) {
                    for (Map<String, Object> tc : (List<Map<String, Object>>) problemConfig.get("testCases")) {
                        AIProblem.TestCase testCase = new AIProblem.TestCase();
                        testCase.setInput((String) tc.get("input"));
                        testCase.setExpected((String) tc.get("expected"));
                        testCase.setHidden(false);
                        testCases.add(testCase);
                    }
                }
                problem.setTestCases(testCases);
                problems.add(problem);
            }
            
            // Save all problems
            aiProblemRepository.saveAll(problems);
            
            response.put("success", true);
            response.put("message", "Problems loaded from YAML successfully");
            response.put("totalProblems", problems.size());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private ResponseEntity<Map<String, Object>> createCategoryQuestions(String category, Runnable questionCreator) {
        Map<String, Object> response = new HashMap<>();
        try {
            questionCreator.run();
            long count = aiProblemRepository.findByActiveTrueAndCategory(category).size();
            response.put("success", true);
            response.put("message", category + " questions added successfully");
            response.put("count", count);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private void createSimpleAICodingQuestions() {
        List<AIProblem> problems = Arrays.asList(
            createAIProblem(
                "Linear Regression Implementation",
                "Implement a simple linear regression model from scratch using gradient descent.",
                Difficulty.EASY,
                "Simple AI Coding",
                Arrays.asList("machine-learning", "regression", "gradient-descent"),
                30,
                75.0,
                "def linear_regression(X, y, learning_rate=0.01, epochs=1000):\n    # Your implementation here\n    pass"
            ),
            createAIProblem(
                "K-Means Clustering",
                "Implement the K-means clustering algorithm to group data points into k clusters.",
                Difficulty.MEDIUM,
                "Simple AI Coding",
                Arrays.asList("machine-learning", "clustering", "unsupervised"),
                45,
                65.0,
                "def kmeans(data, k, max_iters=100):\n    # Your implementation here\n    pass"
            ),
            createAIProblem(
                "Decision Tree Node Split",
                "Implement the logic to find the best split for a decision tree node using information gain.",
                Difficulty.MEDIUM,
                "Simple AI Coding",
                Arrays.asList("machine-learning", "decision-tree", "information-gain"),
                40,
                60.0,
                "def find_best_split(data, labels):\n    # Your implementation here\n    pass"
            ),
            createAIProblem(
                "Naive Bayes Classifier",
                "Implement a Naive Bayes classifier for text classification.",
                Difficulty.EASY,
                "Simple AI Coding",
                Arrays.asList("machine-learning", "classification", "naive-bayes"),
                35,
                70.0,
                "class NaiveBayesClassifier:\n    def __init__(self):\n        pass\n    \n    def fit(self, X, y):\n        # Your implementation here\n        pass\n    \n    def predict(self, X):\n        # Your implementation here\n        pass"
            ),
            createAIProblem(
                "Perceptron Learning",
                "Implement the perceptron learning algorithm for binary classification.",
                Difficulty.EASY,
                "Simple AI Coding",
                Arrays.asList("machine-learning", "perceptron", "binary-classification"),
                25,
                80.0,
                "class Perceptron:\n    def __init__(self, learning_rate=0.01, epochs=100):\n        self.learning_rate = learning_rate\n        self.epochs = epochs\n    \n    def fit(self, X, y):\n        # Your implementation here\n        pass\n    \n    def predict(self, X):\n        # Your implementation here\n        pass"
            )
        );
        aiProblemRepository.saveAll(problems);
    }

    private void createAIDebuggingQuestions() {
        List<AIProblem> problems = Arrays.asList(
            createAIProblem(
                "Fix Overfitting Neural Network",
                "Debug and fix a neural network that is overfitting to the training data.",
                Difficulty.MEDIUM,
                "AI Debugging",
                Arrays.asList("neural-networks", "overfitting", "regularization"),
                60,
                55.0,
                "# Buggy neural network implementation\n# Fix the overfitting issues\nclass NeuralNetwork:\n    def __init__(self):\n        # Implementation with overfitting issues\n        pass"
            ),
            createAIProblem(
                "Memory Leak in Training Loop",
                "Identify and fix memory leaks in a deep learning training loop.",
                Difficulty.MEDIUM,
                "AI Debugging",
                Arrays.asList("deep-learning", "memory-management", "pytorch"),
                45,
                50.0,
                "# Training loop with memory leaks\n# Fix the memory issues\ndef train_model(model, dataloader, epochs):\n    # Buggy implementation\n    pass"
            ),
            createAIProblem(
                "Gradient Vanishing Problem",
                "Debug and solve the vanishing gradient problem in a deep neural network.",
                Difficulty.HARD,
                "AI Debugging",
                Arrays.asList("deep-learning", "gradients", "backpropagation"),
                90,
                40.0,
                "# Deep network with vanishing gradients\n# Fix the gradient flow issues\nclass DeepNetwork:\n    def __init__(self):\n        # Problematic architecture\n        pass"
            ),
            createAIProblem(
                "Data Preprocessing Bug",
                "Find and fix bugs in data preprocessing pipeline that cause model performance issues.",
                Difficulty.MEDIUM,
                "AI Debugging",
                Arrays.asList("data-preprocessing", "feature-engineering", "debugging"),
                30,
                65.0,
                "# Buggy data preprocessing\n# Fix the preprocessing issues\ndef preprocess_data(raw_data):\n    # Implementation with bugs\n    pass"
            ),
            createAIProblem(
                "Model Convergence Issues",
                "Debug why a machine learning model is not converging during training.",
                Difficulty.MEDIUM,
                "AI Debugging",
                Arrays.asList("optimization", "convergence", "learning-rate"),
                50,
                45.0,
                "# Model that won't converge\n# Fix the convergence issues\ndef train_model(X, y, learning_rate, epochs):\n    # Problematic training logic\n    pass"
            )
        );
        aiProblemRepository.saveAll(problems);
    }

    private void createAISystemDesignQuestions() {
        List<AIProblem> problems = Arrays.asList(
            createAIProblem(
                "Scalable ML Pipeline Design",
                "Design a scalable machine learning pipeline for real-time predictions.",
                Difficulty.HARD,
                "AI System Design",
                Arrays.asList("system-design", "ml-pipeline", "scalability"),
                120,
                35.0,
                "# Design a scalable ML pipeline\n# Consider: data ingestion, feature processing, model serving, monitoring\nclass MLPipeline:\n    def __init__(self):\n        # Your design here\n        pass"
            ),
            createAIProblem(
                "Recommendation System Architecture",
                "Design the architecture for a large-scale recommendation system.",
                Difficulty.HARD,
                "AI System Design",
                Arrays.asList("recommendation-system", "collaborative-filtering", "system-architecture"),
                150,
                30.0,
                "# Design recommendation system architecture\n# Consider: user-item interactions, cold start, scalability\nclass RecommendationSystem:\n    def __init__(self):\n        # Your architecture here\n        pass"
            ),
            createAIProblem(
                "Real-time Fraud Detection System",
                "Design a real-time fraud detection system for financial transactions.",
                Difficulty.HARD,
                "AI System Design",
                Arrays.asList("fraud-detection", "real-time", "anomaly-detection"),
                180,
                25.0,
                "# Design real-time fraud detection system\n# Consider: streaming data, low latency, high accuracy\nclass FraudDetectionSystem:\n    def __init__(self):\n        # Your design here\n        pass"
            ),
            createAIProblem(
                "Distributed Model Training",
                "Design a system for distributed training of large deep learning models.",
                Difficulty.HARD,
                "AI System Design",
                Arrays.asList("distributed-training", "deep-learning", "parallel-computing"),
                200,
                20.0,
                "# Design distributed training system\n# Consider: data parallelism, model parallelism, communication\nclass DistributedTrainer:\n    def __init__(self):\n        # Your design here\n        pass"
            ),
            createAIProblem(
                "AI Model Monitoring Platform",
                "Design a comprehensive monitoring platform for ML models in production.",
                Difficulty.HARD,
                "AI System Design",
                Arrays.asList("model-monitoring", "mlops", "production"),
                160,
                30.0,
                "# Design ML model monitoring platform\n# Consider: drift detection, performance metrics, alerting\nclass ModelMonitor:\n    def __init__(self):\n        # Your design here\n        pass"
            )
        );
        aiProblemRepository.saveAll(problems);
    }

    private void createAdvancedAIChallengesQuestions() {
        List<AIProblem> problems = Arrays.asList(
            createAIProblem(
                "Custom Transformer Architecture",
                "Implement a novel transformer architecture with custom attention mechanisms.",
                Difficulty.EXPERT,
                "Advanced AI Challenges",
                Arrays.asList("transformer", "attention", "deep-learning"),
                240,
                15.0,
                "# Implement custom transformer with novel attention\nclass CustomTransformer:\n    def __init__(self):\n        # Your implementation here\n        pass"
            ),
            createAIProblem(
                "Multi-modal Learning System",
                "Build a system that learns from multiple modalities (text, image, audio) simultaneously.",
                Difficulty.EXPERT,
                "Advanced AI Challenges",
                Arrays.asList("multi-modal", "fusion", "representation-learning"),
                300,
                10.0,
                "# Implement multi-modal learning system\nclass MultiModalLearner:\n    def __init__(self):\n        # Your implementation here\n        pass"
            ),
            createAIProblem(
                "Neural Architecture Search",
                "Implement a neural architecture search algorithm to automatically design optimal networks.",
                Difficulty.EXPERT,
                "Advanced AI Challenges",
                Arrays.asList("nas", "automl", "optimization"),
                360,
                8.0,
                "# Implement neural architecture search\nclass NeuralArchitectureSearch:\n    def __init__(self):\n        # Your implementation here\n        pass"
            ),
            createAIProblem(
                "Federated Learning Framework",
                "Design and implement a federated learning framework for privacy-preserving ML.",
                Difficulty.EXPERT,
                "Advanced AI Challenges",
                Arrays.asList("federated-learning", "privacy", "distributed-ml"),
                280,
                12.0,
                "# Implement federated learning framework\nclass FederatedLearning:\n    def __init__(self):\n        # Your implementation here\n        pass"
            ),
            createAIProblem(
                "Quantum Machine Learning",
                "Implement a quantum machine learning algorithm using quantum circuits.",
                Difficulty.EXPERT,
                "Advanced AI Challenges",
                Arrays.asList("quantum-ml", "quantum-computing", "qiskit"),
                400,
                5.0,
                "# Implement quantum ML algorithm\nclass QuantumMLAlgorithm:\n    def __init__(self):\n        # Your implementation here\n        pass"
            )
        );
        aiProblemRepository.saveAll(problems);
    }

    private AIProblem createAIProblem(String title, String description, Difficulty difficulty, 
                                     String category, List<String> tags, int estimatedTime, 
                                     double acceptanceRate, String starterCode) {
        AIProblem problem = new AIProblem();
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setDifficulty(difficulty);
        problem.setCategory(category);
        problem.setTags(tags);
        problem.setEstimatedTime(String.valueOf(estimatedTime));
        problem.setAcceptanceRate(String.valueOf(acceptanceRate));
        problem.setStarterCode(starterCode);
        problem.setActive(true);
        problem.setCreatedAt(LocalDateTime.now());
        problem.setUpdatedAt(LocalDateTime.now());
        problem.setTestCases(new ArrayList<>());
        return problem;
    }
}