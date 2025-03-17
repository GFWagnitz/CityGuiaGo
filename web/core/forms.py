from django import forms
from django.core.validators import FileExtensionValidator
from django.contrib.auth import get_user_model

User = get_user_model()

class ImportAtracoesForm(forms.Form):
    csv_file = forms.FileField(
        label='CSV File',
        validators=[FileExtensionValidator(allowed_extensions=['csv'])],
        help_text='CSV file containing attraction data. Please use the template format.'
    )
    
    images_zip = forms.FileField(
        label='Images ZIP File',
        validators=[FileExtensionValidator(allowed_extensions=['zip'])],
        help_text='ZIP file containing all images referenced in the CSV file.'
    )
    
    user = forms.ModelChoiceField(
        label='User',
        queryset=User.objects.all(),
        required=False,
        help_text='User to associate with uploaded images. Leave blank to not associate with any user.'
    )
    
    encoding = forms.CharField(
        label='CSV Encoding',
        required=False,
        initial='utf-8',
        help_text='The encoding of the CSV file. Default is utf-8.'
    )
    
    delimiter = forms.CharField(
        label='CSV Delimiter',
        required=False,
        initial=',',
        max_length=1,
        help_text='The delimiter used in the CSV file. Default is comma (,).'
    )
    
    skip_header = forms.BooleanField(
        label='Skip Header Row',
        required=False,
        initial=True,
        help_text='Check this if the CSV file contains a header row that should be skipped.'
    ) 