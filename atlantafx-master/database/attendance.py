import random
import datetime
import mysql.connector

# Database connection
conn = mysql.connector.connect(
    host='localhost',
    user='root',
        password='112345678',
    database='eproject2'
)

cursor = conn.cursor()

# Generate attendance records
staff_ids = ['60000001', '60000002', '60000003', '60000004', '60000005', '60000006']

for day in range(1, 31):  # For all days in September
    for staff_id in staff_ids:
        check_in_hour = random.randint(8, 10)
        check_in_minute = random.randint(0, 59)
        check_in = datetime.datetime(2024, 9, day, check_in_hour, check_in_minute)

        check_out_hour = random.randint(5, 7)
        check_out_minute = random.randint(0, 59)
        check_out = datetime.datetime(2024, 9, day, check_out_hour, check_out_minute)

        status = 'On Time' if check_in <= datetime.datetime(2024, 9, day, 9, 0) else 'Late'

        # Insert into attendance table
        cursor.execute("""
            INSERT INTO attendance (staff_id, check_in, check_out, status)
            VALUES (%s, %s, %s, %s)
        """, (staff_id, check_in, check_out, status))

# Commit changes and close the connection
conn.commit()
cursor.close()
conn.close()
