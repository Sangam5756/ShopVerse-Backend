import requests
import json

# Test the notification service
try:
    # Test MongoDB connection
    response = requests.get("http://localhost:8086/api/mongo-test/count")
    print("MongoDB Count Response:", response.status_code, response.text)
    
    # Test sending a notification
    response = requests.post("http://localhost:8086/api/test/send-notification", 
                           params={"email": "test@example.com"})
    print("Send Notification Response:", response.status_code, response.text)
    
    # Check count again
    response = requests.get("http://localhost:8086/api/mongo-test/count")
    print("MongoDB Count After:", response.status_code, response.text)
    
except Exception as e:
    print("Error:", e)
