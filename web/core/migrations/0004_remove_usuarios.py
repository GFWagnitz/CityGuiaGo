# Generated by Django 5.1.6 on 2025-03-16 14:48

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('core', '0003_update_foreign_keys_to_user'),
    ]

    operations = [
        migrations.DeleteModel(
            name='Usuarios',
        ),
    ]
