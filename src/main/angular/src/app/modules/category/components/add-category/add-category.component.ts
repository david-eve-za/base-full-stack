import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CategoryService} from "../../../shared/services/category.service";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-add-category',
  templateUrl: './add-category.component.html',
  styleUrls: ['./add-category.component.css']
})
export class AddCategoryComponent implements OnInit {

  public categoryForm: FormGroup;

  constructor(private fb:FormBuilder, private categoryService: CategoryService,private dialogRef: MatDialogRef<AddCategoryComponent>) {
    this.categoryForm = this.fb.group({
      name: ['',Validators.required],
      description: ['',Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onSave() {

    let data = {
      name: this.categoryForm.get('name')?.value,
      description: this.categoryForm.get('description')?.value
    }

    this.categoryService.createCategory(data).subscribe(data => {
      console.log(data);
      this.dialogRef.close(1);
    }, error => {
      console.log(error);
      this.dialogRef.close(2);
    });

  }

  onCancel() {
    this.dialogRef.close(3);
  }
}
