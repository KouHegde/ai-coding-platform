// Create application user
db = db.getSiblingDB('aicodingdb');

db.createUser({
  user: 'aicodinguser',
  pwd: 'aicodingpass',
  roles: [
    {
      role: 'readWrite',
      db: 'aicodingdb'
    }
  ]
});

// Create indexes for better performance
db.ai_problems.createIndex({ "category": 1 });
db.ai_problems.createIndex({ "difficulty": 1 });
db.ai_problems.createIndex({ "tags": 1 });
db.ai_problems.createIndex({ "active": 1 });