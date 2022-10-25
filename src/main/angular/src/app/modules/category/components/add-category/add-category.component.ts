import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CategoryService} from "../../../shared/services/category.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-add-category',
  templateUrl: './add-category.component.html',
  styleUrls: ['./add-category.component.css']
})
export class AddCategoryComponent implements OnInit {

  public categoryForm: FormGroup;
  formState: string = "";

  constructor(private fb: FormBuilder,
              private categoryService: CategoryService,
              private dialogRef: MatDialogRef<AddCategoryComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {

    console.log(data);

    this.formState = data.id ? "Edit" : "Add";

    this.categoryForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });

    if (data.id) {
      this.categoryForm.patchValue({
        name: data.name,
        description: data.description
      });
    }
  }

  onSave() {

    let data = {
      name: this.categoryForm.get('name')?.value,
      description: this.categoryForm.get('description')?.value
    }

    if (this.data.id) {
      this.categoryService.updateCategory(this.data.id, data).subscribe(data => {
        this.dialogRef.close(1);
      }, error => {
        this.dialogRef.close(2);
      });
    } else {
      this.categoryService.createCategory(data).subscribe(data => {
        console.log(data);
        this.dialogRef.close(1);
      }, error => {
        console.log(error);
        this.dialogRef.close(2);
      });
    }
  }

  onCancel() {
    this.dialogRef.close(3);
  }

  ngOnInit(): void {
    console.log(this.categoryForm);
  }
}
